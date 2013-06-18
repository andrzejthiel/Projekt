package projekt.agents;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TripsFrame extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2988259647989202553L;
	private boolean DEBUG = false;
	JDialog frame;
	Set<OWLNamedIndividual> individuals;
	JTable table;
	ProcessRequests pr = new ProcessRequests();
	public TripsFrame (Set<OWLNamedIndividual> individuals) {
		this.individuals = individuals;
	}
	class Thanks extends JPanel {
		
		private static final long serialVersionUID = -8647868881395842670L;
		public Thanks(Object valueAt) {
			super();
			setLayout(new BorderLayout());
			JLabel background=new JLabel(new ImageIcon(getClass().getResource("/images/travel1.png")));
			
			add(background, BorderLayout.CENTER);
			background.setLayout(new BorderLayout());
			JLabel l = new JLabel("Dzi�kujemy za wybranie wycieczki i �yczymy mi�ych wakacji w " + valueAt.toString() +" :)");
			l.setHorizontalAlignment(SwingConstants.CENTER);
			l.setFont(new Font("Helvetica", Font.BOLD, 19));
			background.add(l, BorderLayout.CENTER);
		}
		
		
	}
	class Frame extends JPanel {

		private static final long serialVersionUID = 1127786648621514430L;

		public Frame() {
			super();
			setLayout(new BorderLayout());
			JLabel background=new JLabel(new ImageIcon(getClass().getResource("/images/travel1.png")));
			add(background, BorderLayout.CENTER);
			background.setLayout(new FlowLayout());
			JButton b = new JButton("Send");
			JButton b2 = new JButton("Back to preferences");
			
			background.add(b);
			background.add(b2);
			String[] columnNames = {"Country",
					"City",
					"Hotel",
					"Date",
					"Duration",
					"Transport",
					"Price"};

			Object[][] data = {};

			table = new JTable();
			DefaultTableModel model = new DefaultTableModel(data, columnNames) {
				@Override
			    public boolean isCellEditable(int row, int column) {
			       return false;
			    }
			};
			table.setModel(model);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setPreferredScrollableViewportSize(new Dimension(600, 360));
			table.setFillsViewportHeight(true);
			if (DEBUG) {
				table.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						printDebugData(table);
					}
				});
			}
			//Set<OWLNamedIndividual> dateInd = pr.getIndividualsFromClass("Date");
			Iterator<OWLNamedIndividual> iterator = individuals.iterator();
			// Lecimy po odebranych individualach (datach)
			while(iterator.hasNext()) {
				String duration = null;
				String hotelName = null;
				String country = null;
				String city = null;
				String cena = null;
				String transport = null;
				OWLNamedIndividual date = iterator.next();
				Map<OWLObjectPropertyExpression, Set<OWLIndividual>> dateProperties = pr.getObjectProperties(date);
				Iterator<OWLObjectPropertyExpression> propertiesIterator = dateProperties.keySet().iterator();
				while(propertiesIterator.hasNext()) {
					OWLObjectPropertyExpression key = propertiesIterator.next();
					if(pr.stripFromIRI(key.toString()).equals("hasDuration")) {
						duration = pr.stripFromIRI(dateProperties.get(key).iterator().next().toString());
					}
					if(pr.stripFromIRI(key.toString()).equals("hasHotelName")) {
						hotelName = pr.stripFromIRI(dateProperties.get(key).iterator().next().toString());
					}
					if(pr.stripFromIRI(key.toString()).equals("hasTransport")) {
						transport = pr.stripFromIRI(dateProperties.get(key).iterator().next().toString());
					}
					if(pr.stripFromIRI(key.toString()).equals("hasCity")) {
						city = pr.stripFromIRI(dateProperties.get(key).iterator().next().toString());
						OWLNamedIndividual cityInd = (OWLNamedIndividual) dateProperties.get(key).iterator().next();
						Map<OWLObjectPropertyExpression, Set<OWLIndividual>> cityProperties = pr.getObjectProperties(cityInd);
						Iterator<OWLObjectPropertyExpression> cityPropertiesIterator = cityProperties.keySet().iterator();
						while(cityPropertiesIterator.hasNext()) {
							OWLObjectPropertyExpression cityKey = cityPropertiesIterator.next();
							if(pr.stripFromIRI(cityKey.toString()).equals("hasCountry")) {
								country = pr.stripFromIRI(cityProperties.get(cityKey).toString());
							}
						}
					}
				}
				Map<OWLDataPropertyExpression, Set<OWLLiteral>> s = pr.getDataValues(date);
				Iterator<OWLDataPropertyExpression> dataIterator = s.keySet().iterator();
				while(dataIterator.hasNext()) {
					OWLDataPropertyExpression dataKey = dataIterator.next();
					if(pr.stripFromIRI(dataKey.toString()).equals("hasPrice")) {
						cena = pr.getPrice(s.get(dataKey).iterator().next().toString());
					}
				}
				Object[] data1 = {country, city, hotelName, pr.stripFromIRI(date.toString()), duration, transport, cena};
				model.addRow(data1);
			}
			//Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);
			//Add the scroll pane to this panel.
			background.add(scrollPane);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					TableModel model = table.getModel();
					table.getSelectedRow();
					Thanks t = new Thanks(model.getValueAt(table.getSelectedRow(), 0));
					t.setOpaque(true);
					frame.setContentPane(t);
					frame.pack();
					AgentClient.finished = true;
					AgentClient.step = 5;
				}
			});
			b2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					//AgentClient agentClient = new AgentClient();
					AgentClient.step = 0;
					frame.dispose();
				}
			});
		}
	}
	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
			}
		}
		System.out.println("--------------------------");
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	public void createAndShowGUI() {
		
		//Create and set up the window.
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame = new JDialog(f, "Trips");
		frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setModal(true);
		//Create and set up the content pane.
		Frame newContentPane = new Frame();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);
		frame.setSize(800, 540);
		frame.setResizable(false);
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}
