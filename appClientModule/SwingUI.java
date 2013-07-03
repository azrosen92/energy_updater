import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class SwingUI extends JPanel {

	private String GOOGLE_FUSION_TABLE_ID = "145TOXaanydD63tvgVMmCVH0hei0NXNCEK8ekZBg";

	private String BASE_URL = "https://www.googleapis.com/fusiontables/v1/query";

	public void run() {
		final JFrame f = new JFrame("Energy Data Uploader");
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setControlButtonsAreShown(false);
		JButton submit = new JButton("submit");
		JLabel lab = new JLabel("Choose file");
		lab.setLabelFor(fileChooser);

		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO take file from JFileChooser and parse data, submitting it to google fusion table
				File inputFile = fileChooser.getSelectedFile();
				if (inputFile.length() > 0) {
					//read contents of file and submit to fusion table
					//TODO: ask for gmail username and password, check for permissions
					JOptionPane.showMessageDialog(f, "File choosen");
					readContentsOfFile(inputFile);
				} else {
					JOptionPane.showMessageDialog(f, "Please choose a file");
				}
			}

		});

		JPanel form = new JPanel();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(form, BorderLayout.NORTH);
		JPanel p = new JPanel();
		p.add(fileChooser);
		p.add(lab);
		p.add(submit);		
		f.getContentPane().add(p, BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);


	}


	/*
	 * reads contents of inputFile and sends them to
	 * google fusion table
	 */
	protected void readContentsOfFile(File inputFile) {
		String date = "";
		HashMap<Integer, Double> data = new HashMap<Integer, Double>();


		importIntoFusionTable(date, data);
	}


	/*
	 * creates a row in the fusion table containing the date for that month
	 * and the values from the data hashmap
	 */
	private void importIntoFusionTable(String date,
			HashMap<Integer, Double> data) {
		String columnNames = "(Date";
		String columnValues = "(" + date;
		for (Map.Entry<Integer, Double> entry : data.entrySet()) {
			int buildingId = entry.getKey().intValue();
			double energyData = entry.getValue().doubleValue();
			columnNames = columnNames + ", " + buildingId;
			columnValues = columnValues + ", " + energyData;
		}
		columnNames = columnNames + ")";
		columnValues = columnValues + ")";

		String queryString = "INSERT INTO" + GOOGLE_FUSION_TABLE_ID + columnNames + " VALUES " + columnValues + ";";

		URL url;
		HttpURLConnection connection = null;
		try {
			//create connection
			url = new URL(BASE_URL + "?sql=" + queryString);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.flush ();
			wr.close ();

			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}

	}


}
