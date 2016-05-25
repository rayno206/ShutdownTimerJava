import java.io.IOException;

public class ShutdownFunc {
	
	private String type = "";
	private Runtime runtime = Runtime.getRuntime();
    private Process process = null;
    private static native boolean SetSuspendState(boolean hiberate, boolean forceCritical, boolean disableWakeEvent);
	
	public ShutdownFunc(String PType) {
		this.type = PType;
	}
	
	public void processSDF() {
		if (this.type.compareToIgnoreCase("timing shutdown") == 0) {
			System.out.println("A");
			try {
				process = runtime.exec("shutdown -s -f -t 0");
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (this.type.compareToIgnoreCase("timing restart") == 0) {
			System.out.println("B");
			try {
				process = runtime.exec("shutdown -r -f -t 0");
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (this.type.compareToIgnoreCase("timing sleep") == 0) {
			System.out.println("C");
			SetSuspendState(false, true, true);
		}
	}
}
