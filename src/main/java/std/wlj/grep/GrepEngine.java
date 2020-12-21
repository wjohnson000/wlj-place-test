package std.wlj.grep;

//
//  Simple "GREP" application
//    It will search for a given text string in a list of files.
//    it functions much like its cousin down on UNIX, though
//    without the full capability of specifying a regular expression
//    as part of the search pattern.
//
//  This version will recursively search all subdirectories, it will
//  strip "ugly" characters from the lines (in the case of binary files)
//  and can trim leading characters and/or display a maximum number
//  of characters in the matching line.
//
//  It can also drill down into an archive file, either a ".zip", ".jar",
//  ".war" or ".ear".  It handles these recursively, so it'll search a
//  ".jar" file that's part of a ".war" file.
//
//  @author  Wayne Johnson
//  @date    06 December 2000
//

import java.io.*;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


// Main application class
public class GrepEngine extends Observable {

	int     maxChar       = 0;       // Maximum number of characters to show in a long line
	boolean realTime      = false;   // Notify listeners of results in real-time
	boolean showLine      = false;   // Show the line-number in file of where match happened
	boolean ignoreCase    = false;   // Ignore upper- and lower-case on matches
	boolean showMatch     = true;    // Show details of the match [if FALSE, list file name only]
	boolean trimData      = false;   // Trim the data before returning results
	boolean invertMatch   = false;   // Show lines/files that DO NOT match
	boolean ignoreTarget  = false;   // Skip any directory with 'target' in the name
	boolean ignoreTest    = false;   // Skip any directory with 'test' in the name
	boolean ignoreQA      = false;   // Skip any directory with 'qa' in the name
	boolean ignoreHidden  = false;   // Skip any hidden directory, i.e., starts with '.'
	boolean searchArchive = false;   // Search the contents of archive files
	boolean fileNameMatch = false;   // Match the file NAME, not the file CONTENTS

	String  filter        = null;    // Filename filter
	String  outputDelim   = " ";     // Default output delimiter

	private boolean isRunning = false;
	private long startNanos   = 0;   // Nanosecond start time
	private int matchCount    = 0;   // Total number of matches returned
	private int dirCount      = 0;   // Total number of directories searched
	private int fileCount     = 0;   // Total number of files looked at
	private int checkCount    = 0;   // Total number of files that passed the filename filter
	private StringBuilder sbuf = new StringBuilder(1024);


	/**
	 * Default constructor ... does nothing.
	 */
	public GrepEngine() { }

	/**
	 * Create an engine that can return results in real-time
	 * @param realTime flag indicating if results are to be returned in real-time
	 */
	public GrepEngine(boolean realTime) {
		this.realTime = realTime;
	}


	/**
	 * Main search method ... for non-observers the results of
	 * the search are returned here.  Observers can get real-time
	 * results.
	 * 
	 * @param fname single file name or directory path
	 * @param match pattern to match against
	 * @return full results of the GREP
	 */
	public String doSearch(String fname, String match) {
		matchCount = 0;
		dirCount   = 0;
		fileCount  = 0;
		checkCount = 0;
		startNanos = System.nanoTime();
		isRunning  = true;
		sbuf       = new StringBuilder(1024);
		doSearchInt(fname, match);
		setChanged();
		notifyObservers(null);
		return sbuf.toString();
	}


	/**
	 * Method to stop the GREP process
	 */
	public void stopSearch() {
		isRunning = false;
	}


	/**
	 * Get an array with values of file counts:
	 *    -- execute time, in nanoseconds
	 *    -- number of matches
	 *    -- number of directories scanned
	 *    -- number of files encountered
	 *    -- number of files matching the filter
	 * 
	 * @return array of 'int'
	 */
	public long[] getSearchMetrics() {
		return new long[] { System.nanoTime()-startNanos, matchCount, dirCount, fileCount, checkCount };
	}


	/**
	 * Recursive method that searches for a pattern match in the
	 * given file or directory.
	 * 
	 * @param fname single file name or directory path
	 * @param match pattern to match against
	 */
	private void doSearchInt(String fname, String match) {
		// If we've been instructed to halt, please do so ...
		if (! isRunning) {
			return;
		}

		File file = null;
		String adjMatch = (ignoreCase) ? match.toLowerCase() : match;

		try {
			file = new File(fname);
			addResult(file);

			// Check for hidden file or directory ... return immediately
			if (ignoreHidden  &&  file.getName().startsWith(".")) {
				;  // Do nothing ... just return

			// If this is a directory, recursively search all files in the directory
			} else if (file.isDirectory()) {
				if (ignoreTarget == false  ||  fname.indexOf("target") == -1) {
					if (ignoreTest == false  ||  fname.indexOf("test") == -1) {
						if (ignoreQA == false  ||  fname.indexOf("qa") == -1) {
							dirCount++;
							String[] dirList = file.list();
							for (int i = 0; i < dirList.length; i++) {
								doSearchInt(fname + File.separator + dirList[i], match);
							}
						}
					}
				}

			// If this is an archive (.zip, .jar, .war, .ear) dive into the contents
			} else if (searchArchive  &&  isJavaArchive(fname)) {
				processJavaArchive(fname, fname, adjMatch);
				
			// Process this if the name matches what we're looking for
			} else {
				fileCount++;
				if (isFilenameOk(fname)) {
					checkCount++;
					if (fileNameMatch) {
						boolean nameMatch = (ignoreCase) ? fname.toLowerCase().contains(adjMatch) : fname.contains(adjMatch);
						if (nameMatch ^ invertMatch) {
							addResult(fname);
						}
					} else {
						processFile("", fname, adjMatch);
					}
				}
			}
		} catch (Exception ee) {
			addResult("Unable to open file: " + fname);
		}
	}

	/**
	 * Determine if the file-name is one that we wish to process
	 * 
	 * @param fileName file-name
	 * @return TRUE if we should process the file, FALSE otherwise
	 */
	private boolean isFilenameOk(String fileName) {
		// See if this is a file we want to process ...
		int pos = fileName.lastIndexOf('.');
		if (pos > 0   &&   filter != null) {
			String ext = fileName.substring(pos);
			if (filter.toLowerCase().indexOf(ext.toLowerCase()) == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine if the file is a Java archive, which will be either a
	 * ".jar", ".war", ".ear" or ".zip" file.
	 * 
	 * @param fname file-name
	 * @return TRUE if this is a Java archive, FALSE otherwise
	 */
	private boolean isJavaArchive(String fname) {
		return (fname.endsWith(".jar")  ||  fname.endsWith(".war")  ||
				fname.endsWith(".ear")  ||  fname.endsWith(".zip"));
	}

	/**
	 * Process an archive, looping through each of the entries and checking
	 * them in turn.  This will be similar to what is done in the
	 * "doSearchInt()" method.  Nested Java archives will be processed
	 * in the same manner.
	 * 
	 * @param prefix prefix used when displaying the results.
	 * @param filePath file path (full path + name)
	 * @param adjMatch the match-string, in the correct case for matching
	 */
	private void processJavaArchive(String prefix, String filePath, String adjMatch) {
		FileInputStream inStr = null;
		try {
			inStr = new FileInputStream(filePath);
			processJavaArchive("", filePath, inStr, adjMatch);
		} catch (Exception ee) {
		} finally {
			if (inStr != null) try { inStr.close(); } catch (Exception ex) { }
		}
	}

	/**
	 * Process an archive, looping through each of the entries and checking
	 * them in turn.  This will be similar to what is done in the
	 * "doSearchInt()" method.  Nested Java archives will be processed
	 * in the same manner.
	 * 
	 * @param prefix prefix used when displaying the results.
	 * @param fname file name
	 * @param inStr input-stream with data
	 * @param adjMatch the match-string, in the correct case for matching
	 */
	private void processJavaArchive(String prefix, String fileName, InputStream inStr, String adjMatch) {
		// If we've been instructed to halt, please do so ...
		if (! isRunning) {
			return;
		}
		ZipEntry zEntry = null;
		ZipInputStream zis = null;
		String mmSlash = adjMatch.replace('.', '/');

		// Check the file name
		fileCount++;
		if (fileNameMatch) {
			boolean nameMatch = (ignoreCase) ? fileName.toLowerCase().contains(adjMatch) : fileName.contains(adjMatch);
			if (nameMatch ^ invertMatch) {
				addResult(prefix + fileName);
			}
		}

		// Run through the file archive contents
		try {
			zis = new ZipInputStream(inStr);
			while ((zEntry = zis.getNextEntry()) != null) {
				fileCount++;
				String zFileName = zEntry.getName();
				if (fileNameMatch) {
					boolean nameMatch = (ignoreCase) ? zFileName.toLowerCase().contains(adjMatch) : zFileName.contains(adjMatch);
					if (! nameMatch) {
						nameMatch = (ignoreCase) ? zFileName.toLowerCase().contains(mmSlash) : zFileName.contains(mmSlash);
					}
					if (nameMatch ^ invertMatch) {
						addResult(prefix + fileName + "::" + zFileName);
					}
				}

				if (! fileNameMatch  ||  isJavaArchive(zFileName)) {
					byte[] bytes = new byte[10240];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int len = zis.read(bytes, 0, bytes.length);
					while (len > 0) {
						baos.write(bytes, 0, len);
						len = zis.read(bytes, 0, bytes.length);
					}

					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					if (searchArchive  &&  isJavaArchive(zFileName)) {
						processJavaArchive(prefix + fileName + "::", zFileName, bais, adjMatch);
					} else if (isFilenameOk(zFileName)) {
						fileCount++;
						processFile(prefix + fileName + "::", zFileName, bais, adjMatch);
					}
				}
			}
		} catch (IOException ioex) {
			System.out.println("OUCH !! ... " + ioex);
		} finally {
			if (zis != null) try { zis.close(); } catch (Exception ex) { }
		}
		
	}

	/**
	 * Process an entry, given a file name and the input-stream to the raw
	 * data.
	 * 
	 * @param prefix prefix used when displaying the results.
	 * @param filePath file-name (path + name)
	 * @param adjMatch the match-string, in the correct case for matching
	 */
	private void processFile(String prefix, String filePath, String adjMatch) {
		FileInputStream inStr = null;
		try {
			inStr = new FileInputStream(filePath);
			processFile(prefix, filePath, inStr, adjMatch);
		} catch (Exception ee) {
		} finally {
			if (inStr != null) try { inStr.close(); } catch (Exception ex) { }
		}
	}

	
	/**
	 * Process an entry, given a file name and the input-stream to the raw
	 * data.  This slight abstraction allows us to process a file whether
	 * it's from the hard drive or from an archival file.
	 * 
	 * @param prefix prefix used when displaying the results.
	 * @param fileName file-name
	 * @param inStr input-stream with data
	 * @param adjMatch the match-string, in the correct case for matching
	 */
	private void processFile(String prefix, String fileName, InputStream inStr, String adjMatch) {
		int lineno = 0;
		boolean more = true;
		BufferedReader inBuf;
		String text, textx;

		checkCount++;
		inBuf = null;
		try {
			int ipos = 0;
			boolean hasMatch = false;
			inBuf = new BufferedReader(new InputStreamReader(inStr), 1024);
			while (inBuf.ready()  &&  more) {
				lineno++;
				text = inBuf.readLine();
				text = cleanText(text);
				textx = (ignoreCase) ? text.toLowerCase() : text;

				// Use a simple method for finding target string
				if ((ipos = textx.indexOf(adjMatch)) >= 0) {
					hasMatch = true;
					if (showMatch) {
						if (trimData) {
							text = text.trim();
						}
						if (maxChar > 0) {
							if (ipos > maxChar) {
								text = "..." + text.substring(ipos-maxChar);
								ipos = maxChar + 3;
							}
							if (ipos+adjMatch.length()+maxChar < text.length()) {
								text = text.substring(1, ipos+adjMatch.length()+maxChar) + "...";
							}
						}
						if (showLine) {
							addResult(prefix + fileName + ":" + lineno + outputDelim + text);
						} else {
							addResult(prefix + fileName + outputDelim + text);
						}
					} else {
						if (invertMatch) {
							// do nothing ...
						} else if (showLine) {
							addResult(prefix + fileName + ":" + lineno);
						} else {
							addResult(prefix + fileName);
						}
						more = false;
					}
				}
			}
			if (!hasMatch  &&  invertMatch) {
				addResult(prefix + fileName);
			}
		} catch (Exception ee) {
		} finally {
			if (inBuf != null) try { inBuf.close(); } catch (Exception ex) { }
		}

	}

	/**
	 * Replace all non-ASCII characters with a ".", in case we are
	 * searching silly files
	 * 
	 * @param text String data
	 * @return String data with all non-ASCII characters coded as "."
	 */
	private String cleanText(String text) {
		StringBuffer sb = new StringBuffer(text.length());
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == 9  ||  ch == 160) {
				sb.append(ch);
			} else if (ch >= 32  &&  ch < 128) {
				sb.append(ch);
			} else { 
				sb.append("<" + ((int)ch) + ">");
			}
		}
		return sb.toString();
	}

	/**
	 * Notify all observers that something exciting has happened
	 * 
	 * @param something
	 */
	private void addResult(Object something) {
		if (realTime) {
			setChanged();
			notifyObservers(something);
		}

		if (something != null  &&  something instanceof String) {
			matchCount++;
			sbuf.append(something + "\n");
		}
	}
}
