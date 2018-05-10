package std.wlj.date.v1;

import org.familysearch.standards.core.logging.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Provides access to data required for date standardization.
 *
 * @author Pete Blake
 */
public class DateDataAccess {

  ///////Constants
  /**
   * Relative path from the shared data dir in which to find date data files.
   */

  private static final String MONTH_FILENAME = "/org/familysearch/standards/date/dataaccess/MonthNames.xml";
  private static final String MODIFIER_FILENAME = "/org/familysearch/standards/date/dataaccess/Modifiers.xml";

//  private static final String CJK_CALENDAR_FILENAME = "/org/familysearch/standards/date/dataaccess/cjkCalendarTable.xml";
  private static final String CJK_CALENDAR_FILENAME = "/std/wlj/date/v1/cjkCalendarTable.xml";
//  private static final String JP_CALENDAR_FILENAME = "/org/familysearch/standards/date/dataaccess/jpCalendarTable.xml";

  // Name of the file containing emperor names
  // package access to support testability
  static final String ZH_EMPEROR_FILENAME = "/org/familysearch/standards/date/dataaccess/zhNames.xml";
  static final String JP_EMPEROR_FILENAME = "/org/familysearch/standards/date/dataaccess/jpNames.xml";
  static final String KO_EMPEROR_FILENAME = "/org/familysearch/standards/date/dataaccess/koNames.xml";

  // French Republican Calendar files
  private static final String FRC_MONTH_FILENAME = "/org/familysearch/standards/date/dataaccess/FRCMonthNames.xml";
  private static final String FRC_ORDINAL_FILENAME = "/org/familysearch/standards/date/dataaccess/FRCOrdinals.xml";
  private static final String FRC_COMP_DAY_FILENAME = "/org/familysearch/standards/date/dataaccess/FRCCompDays.xml";

  // Properties support
  private static final String AUTHORITIES_DATE_PROPERTIES_FILENAME = "/org/familysearch/standards/date-version.properties";
  private static final String AUTHORITIES_DATE_VERSION = "current.date.version";
  static final String DEFAULT_AUTHORITIES_DATE_VERSION = "1.0.0";  // package visible for testing purposes
  private static final String AUTHORITES_DATE_VERSION;
  private static final Logger LOGGER = new Logger(DateDataAccess.class);
  private static final DateDataAccess INSTANCE = new DateDataAccess();
  private static final Properties DATE_AUTHORITIES_PROPERTIES = new Properties(getDefaultProperties());


  static {
      InputStream stream=null;
    try {
        stream =DateDataAccess.class.getResourceAsStream(AUTHORITIES_DATE_PROPERTIES_FILENAME);
        DATE_AUTHORITIES_PROPERTIES.load(stream);
    }
    // we will grammars to the grammars properties if we fail
    catch (FileNotFoundException e) {
      LOGGER.warn(e.getMessage(),e);
    }
    catch (IOException e) {
      LOGGER.warn(e.getMessage(),e);
    }
    finally{//Findbug fix
        try{
            if (stream != null){
                stream.close();
            }
        }catch (IOException ioe){
          LOGGER.warn(ioe.getMessage(),ioe);
        }
    }
    AUTHORITES_DATE_VERSION = DATE_AUTHORITIES_PROPERTIES.getProperty(AUTHORITIES_DATE_VERSION);
  }

  ////// Instance Members

  ///// Constructors and factory methods

  public static synchronized DateDataAccess getInstance() {
    //TODO - fix this to be a Dynamic Singleton when the pattern is perfected
    return INSTANCE;
  }

  ///CLOVER:OFF

  private DateDataAccess() {
  }

  ///CLOVER:ON


  public Reader getCJKCalendarReader() throws FileNotFoundException {
    return getReaderUTF8(CJK_CALENDAR_FILENAME);
  }

  public Reader getMonthReader() throws FileNotFoundException {
    return getReaderUTF8(MONTH_FILENAME);
  }

  public Reader getModifierReader() throws FileNotFoundException {
    return getReaderUTF8(MODIFIER_FILENAME);
  }

  public Reader getChEmperorReader() throws FileNotFoundException {
    return getReaderUTF8(ZH_EMPEROR_FILENAME);
  }

  public Reader getJpEmperorReader() throws FileNotFoundException {
    return getReaderUTF8(JP_EMPEROR_FILENAME);
  }

  public Reader getKoEmperorReader() throws FileNotFoundException {
    return getReaderUTF8(KO_EMPEROR_FILENAME);
  }

  public Reader getFRCMonthReader() throws FileNotFoundException {
    return getReaderUTF8(FRC_MONTH_FILENAME);
  }

  public Reader getFRCOrdinalReader() throws FileNotFoundException {
    return getReaderUTF8(FRC_ORDINAL_FILENAME);
  }

  public Reader getFRCCompDaylReader() throws FileNotFoundException {
    return getReaderUTF8(FRC_COMP_DAY_FILENAME);
  }

  public String getAuthoritiesDateVersion() {
    return AUTHORITES_DATE_VERSION;
  }

  private static Properties getDefaultProperties() {
    Properties defaultProps = new Properties();
    defaultProps.setProperty(AUTHORITIES_DATE_VERSION, DEFAULT_AUTHORITIES_DATE_VERSION);
    return defaultProps;
  }

  ///CLOVER:OFF

  private Reader getReaderUTF8(String readerName) {
    return new InputStreamReader(this.getClass().getResourceAsStream(readerName), Charset.forName("UTF-8"));
  }


  ///CLOVER:ON
}
