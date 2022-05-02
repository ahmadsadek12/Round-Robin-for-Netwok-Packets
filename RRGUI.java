import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RRGUI {

	private JFrame frmRoundRobinScheduler;
	private JTextField textField;
	List packet_length = new ArrayList<Integer>();
	List arrival_time = new ArrayList<Long>();
	List packets = new ArrayList<String>();
	int quantum = 2;
	long x = System.currentTimeMillis()/1000;
	long y =0;
	static long a = System.currentTimeMillis()/1000;
	static long b=0;
	static int count = 0;	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RRGUI window = new RRGUI();
					window.frmRoundRobinScheduler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RRGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRoundRobinScheduler = new JFrame();
		frmRoundRobinScheduler.getContentPane().setBackground(Color.WHITE);
		frmRoundRobinScheduler.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Write your message in the textbox bellow");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		lblNewLabel.setBounds(150, 88, 232, 26);
		frmRoundRobinScheduler.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					packet_length.add(textField.getText().length());
					packets.add(textField.getText());
					x = Math.abs(x-System.currentTimeMillis()/1000);
					x+=y;
					arrival_time.add(x);
					textField.setText("");
					y = x;
					x= System.currentTimeMillis()/1000;
				}
			}
		});
		textField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		textField.setBounds(150, 125, 232, 26);
		frmRoundRobinScheduler.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton Done = new JButton("Done");
		Done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object [] objpl = packet_length.toArray();
				Object [] objart = arrival_time.toArray();
				Object [] objp = packets.toArray();
				int [] pl = new int[objpl.length];
				long [] art = new long[objpl.length];
				String[] p = new String[objpl.length];
				for(int i=0; i<objpl.length; i++){
					p[i] = objp[i].toString();
			         pl[i] = (int) objpl[i];
			         art[i] = (long) objart[i];
			      }
				int n = pl.length;
				
				findavgTime(p, n, pl, art, quantum);
			}
		});
		Done.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		Done.setBounds(215, 162, 106, 32);
		frmRoundRobinScheduler.getContentPane().add(Done);
		frmRoundRobinScheduler.setResizable(false);
		frmRoundRobinScheduler.setTitle("Round Robin Scheduler");
		frmRoundRobinScheduler.setBackground(Color.WHITE);
		frmRoundRobinScheduler.setBounds(100, 100, 529, 338);
		frmRoundRobinScheduler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
		
	// Method to find the waiting time for all
		// packets
		static void findWaitingTime(int n,
					int pl[], int wt[], int quantum)
		{
			// Make a copy of packet length pl[] to store remaining packet lengths.
			int rem_pl[] = new int[n];
			for (int i = 0 ; i < n ; i++)
				rem_pl[i] = pl[i];
		
			int t = 0; // Current time
		
			// Keep traversing packets in round robin manner
			// until all of them are not done.
			while(true)
			{
				boolean done = true;
		
				// Traverse all packets one by one repeatedly
				for (int i = 0 ; i < n; i++)
				{
					// If length of a packet is greater than 0
					// then only need to process further
					if (rem_pl[i] > 0)
					{
						done = false; // There is a pending packet
		
						if (rem_pl[i] > quantum)
						{
							// Increase the value of t i.e. shows
							// how much time a packet has been processed
							t += quantum;
		
							// Decrease the length of current packet by quantum
							rem_pl[i] -= quantum;
						}
		
						// If packet length is smaller than or equal to
						// quantum. Last cycle for this packet
						else
						{
							// Increase the value of t i.e. shows
							// how much time a packet has been processed
							t = t + rem_pl[i];
		
							// Waiting time is current time minus time
							// used by this packet
							wt[i] = t - pl[i];
		
							// As the packet gets fully executed
							// make its remaining length = 0
							rem_pl[i] = 0;
						}
					}
				}
		
				// If all packets are done
				if (done == true)
				break;
			}
		}
		
		// Method to calculate turn around time
		static void findTurnAroundTime(int n,
								int pl[], int wt[], long[] art, int tat[])
		{
			// calculating turnaround time by adding
			// pl[i] + wt[i] + at[i]
			for (int i = 0; i < n ; i++)
				tat[i] = (int) (pl[i] + wt[i] + art[i]);
		}
		
		// Method to calculate average time
		static void findavgTime(String [] p, int n, int pl[], long art[],
											int quantum)
		{
			int wt[] = new int[n], tat[] = new int[n];
			int total_wt = 0, total_tat = 0;
		
			// Function to find waiting time of all packets
			findWaitingTime(n, pl, wt, quantum);
		
			// Function to find turn around time for all packets
			findTurnAroundTime(n, pl, wt, art, tat);

			
			int period = 1000; // repeat every sec.
			    Timer timer = new Timer();
			    timer.scheduleAtFixedRate(new TimerTask()
			        {
			            public void run()
			            {
			               // Your code

			                count++;
			                for(int i=0; i<p.length; i++) {
			    				if(tat[i]==count) {
			    					JOptionPane.showMessageDialog(null, p[i]);
			    				}
			    			}
			                

			            }
			        }, 0, period);
			

			// Display packets along with all details
			System.out.println("Message " + " Arrival time " + " Packet length " +
						" Waiting time " + " Turn around time");

			// Calculate total waiting time and total turn
			// around time
			for (int i=0; i<n; i++)
			{
				total_wt = total_wt + wt[i];
				total_tat = total_tat + tat[i];
				System.out.println(p[i] + "\t\t" + art[i] + "\t\t" + pl[i] +"\t\t" +
								wt[i] +"\t\t " + tat[i]);
			}

		}

}