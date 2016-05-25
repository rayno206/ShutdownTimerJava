import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;

public class TimeCountdown extends Thread{
	
	private Spinner spHTCD, spMTCD, spSTCD;
	private String comboBoxTCD;
	private ProgressBar barTCD;
	private ProgressIndicator piTCD;
	private long total = 0;
	private long count;
	private long time[];
	private double percentageProgress = 0;
	private boolean stop = false;
	private ShutdownFunc sdf;
	
	public TimeCountdown(Spinner spHour, Spinner spMinute, Spinner spSecond, String comboBox, long timeleft, ProgressBar bar, ProgressIndicator pi) {
		this.time = new long[3];
		this.time[0] = Long.parseLong(spHour.getEditor().getText());
		this.time[1] = Long.parseLong(spMinute.getEditor().getText());
		this.time[2] = Long.parseLong(spSecond.getEditor().getText());
		this.spHTCD = spHour;
		this.spMTCD = spMinute;
		this.spSTCD = spSecond;
		this.comboBoxTCD = comboBox;
		this.barTCD = bar;
		this.piTCD = pi;
		this.total = timeleft;
		this.count = 0;
	}
	
	@Override
	public void run() {
		sdf = new ShutdownFunc(comboBoxTCD);
		while(stop!=true) {
			if((time[0] + time[1] + time[2]) >= 0) {
				try {
					if(time[2] < 0) {
						if(time[1] > 0){
							time[1]--;
							time[2] = 59;
						}else if(time[1] <= 0) {
							time[0]--;
							time[1] = 59;
							time[2] = 59;
						}
					}
					this.spHTCD.getEditor().setText(String.valueOf(this.time[0]));
					this.spMTCD.getEditor().setText(String.valueOf(this.time[1]));
					this.spSTCD.getEditor().setText(String.valueOf(this.time[2]));
					this.percentageProgress = Double.parseDouble(String.valueOf(count))/Double.parseDouble(String.valueOf(total));
					this.barTCD.setProgress(percentageProgress);
					this.piTCD.setProgress(percentageProgress);
					time[2]--;
					this.count++;
					Thread.sleep(1000);
				} catch (Exception e) {
					System.out.println(e);
				}
			}else {
				sdf.processSDF();
				System.out.println("Shutdown Executed");
				stopMe();
			}	
		}
	}
	
	public void stopMe() {
		stop = true;		
	}
	
	public void startMe() {
		stop = false;
	}
}
