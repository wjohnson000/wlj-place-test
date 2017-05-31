package std.wlj.general;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class JMXStuff {

	private static final String[] MBEAN_NAMES = {
        "java.lang:type=Runtime",
        "java.lang:type=Threading",
        "java.lang:type=Memory",
	};

	public static void main(String... args) throws Exception {
		handleThreads();
		
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		System.out.println("MBS: " + mbs);
		System.out.println("cnt: " + mbs.getMBeanCount());

		Set<ObjectName> beanNames = mbs.queryNames(null, null);
//		for (ObjectName bName : beanNames) {
//			System.out.println("  bn: " + bName.getCanonicalName() + " --> " + bName.getKeyPropertyListString());
//		}

//		for (ObjectName bName : beanNames) {
//			String canonName = bName.getCanonicalName();
//			for (String targetName : MBEAN_NAMES) {
//				if (canonName.equals(targetName)) {
//					MBeanInfo beanInfo = mbs.getMBeanInfo(bName);
//					ObjectInstance beanInstance = mbs.getObjectInstance(bName);
//					System.out.println("======================================================================");
//					System.out.println("  BI: " + beanInfo);
//					System.out.println("  BI: " + beanInfo.getDescription());
//					for (MBeanOperationInfo beanOper : beanInfo.getOperations()) {
//						System.out.println("  BO.name: " + beanOper.getName());
//						System.out.println("  BO.desc: " + beanOper.getDescription());
//						System.out.println("  BO.retn: " + beanOper.getReturnType());
//					}
//					System.out.println("  BI: " + beanInstance);
//					System.out.println("  BI: " + beanInstance.getObjectName());
//					System.out.println("  BI: " + beanInstance.getClassName());
//				}
//			}
//		}
	}

	private static void handleThreads() {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		int thrCnt    = threadBean.getThreadCount();
		int peakCnt   = threadBean.getPeakThreadCount();
		int daemonCnt = threadBean.getDaemonThreadCount();

		System.out.println("CNT: " + thrCnt);
		System.out.println("PEAK: " + peakCnt);
		System.out.println("DAEMON: " + daemonCnt);

		long[] threadIds = threadBean.getAllThreadIds();
		for (long threadId : threadIds) {
			long threadTime = threadBean.getThreadCpuTime(threadId);
			System.out.println("ID: " + threadId + " --> " + threadTime/1000000.0);
		}
	}
}
