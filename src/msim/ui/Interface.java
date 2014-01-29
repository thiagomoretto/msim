package msim.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import msim.dp.MachineFacade;
import msim.dp.MachineObserver;

class BinaryFilter extends javax.swing.filechooser.FileFilter {
	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		return (f.getName().endsWith(".lmq"));
	}

	@Override
	public String getDescription() {
		return "MSIM Binary File (*.lmq)";
	}
}

class SnapShotFilter extends javax.swing.filechooser.FileFilter {
	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		return (f.getName().endsWith(".snp"));
	}

	@Override
	public String getDescription() {
		return "MSIM Snapshot File (*.snp)";
	}
}

/**
 * MIPS Simulator
 * 
 * @author Thiago Moretto <thiago at moretto.eng.br>
 * @version 0.1
 */
public class Interface implements MachineObserver {

	private JFrame controller;
	
	private JButton next, stop, reset,cont;
	
	private List<Displayable> list = new LinkedList<Displayable>();
	
	private MachineFacade facade;
	
	private Displayable memoryui, registersui;
	
	private Console console = new Console();
	
	private File lastSelectedFile;
	
	public Interface( MachineFacade f ) {
		facade = f;
		
		lastSelectedFile = new File(System.getProperty("user.dir"));
		
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu( "File" );
		
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc= new JFileChooser ();
				fc.addChoosableFileFilter(new BinaryFilter());
				if (lastSelectedFile!=null)
					fc.setCurrentDirectory(lastSelectedFile);

				int returnVal = fc.showOpenDialog(controller.getComponent(0));
				if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	lastSelectedFile  = fc.getSelectedFile();
	                try {
						facade.load(new FileInputStream(lastSelectedFile));
						facade.requestResetMachine();
						refresh();
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(controller, "Arquivo não encontrado.");
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(controller, "Erro de entrada e saída, verifique a saída padrão para verificar o erro.");
					}
	            }		
			} 
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		JMenuItem mksnap = new JMenuItem("Make snapshot");
		mksnap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc= new JFileChooser ();
				fc.addChoosableFileFilter(new SnapShotFilter());
				if (lastSelectedFile!=null)
					fc.setCurrentDirectory(lastSelectedFile);
				
				int returnVal = fc.showOpenDialog(controller.getComponent(0));
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
					facade.makeSnapshot( file );
					updateSnapshot();
	            }
			}
		});
		
		JMenuItem rdsnap = new JMenuItem("Read snapshot");
		rdsnap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc= new JFileChooser ();
				fc.addChoosableFileFilter(new SnapShotFilter());
				if (lastSelectedFile!=null)
					fc.setCurrentDirectory(lastSelectedFile);

				int returnVal = fc.showOpenDialog(controller.getComponent(0));
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
					facade.readSnapshot( file );
					updateSnapshot();
	            }
			}
		});
		
		file.add(open);
		file.addSeparator();
		file.add(mksnap);
		file.add(rdsnap);
		file.addSeparator();
		file.add(exit);
		
		menubar.add(file);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		controller = new JFrame( "Machine Simulator: MIPS" );
		controller.setLayout(new FlowLayout());
		controller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		controller.setSize(315, 85);
		controller.setJMenuBar(menubar);
		controller.setLocation((int) screen.getWidth()/2 - 300,(int) screen.getHeight()/2 - 85);
		controller.setResizable(true);
		
		createComponents();
	}
	
	private void createComponents() {
		next  = new JButton("Step");
		stop  = new JButton("Stop");
		reset = new JButton("Reset");
		cont = new JButton("Continue");
		
		memoryui = new MemoryUI(facade);
		addDisplayable(memoryui);
		
		registersui = new RegistersUI(facade.getRegisters());
		addDisplayable(registersui);
		
		next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				facade.requestNextInstruction();
				refresh();
			}
		});
		
		controller.add(next);
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				facade.requestStopMachine();
				refresh();
			}
			
		});
		
		controller.add(stop);
		
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				facade.requestResetMachine();
				refresh();
			}
		});
		
		controller.add(reset);
		
		cont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				facade.requestContinueMachine();
				refresh();
			}
		});
		
		controller.add(cont);
		addDisplayable(console);
	}
	

	public void addDisplayable( Displayable d ) {
		list.add(d);
	}

	public void refresh() {
		for (Displayable d : list) {
			d.refresh();
		}
	}
	
	public void displayAll() {
		for (Displayable d : list) {
			d.display();
		}
		controller.setVisible(true);
	}

	public void notifyExecution() {
		refresh();
	}

	public void notifyMessage(String message) {
		console.addText( message );
	}

	public void updateSnapshot() {
		memoryui.update( facade );		
		registersui.update( facade.getRegisters() );
		refresh();
	}
}
