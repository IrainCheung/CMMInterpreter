package cmmui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import error.CmmException;
import intermediatecode.GenerateCode;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import parser.TreeNode;
import semantic.SemanticParser;
import util.MyOutputStream;

/*
 * 1.��ɻ�����UI��ܴ��ʵ�ּ򵥵��﷨������ʵ�ִ��ļ�����
 * 2.ʵ��ʣ��˵�ѡ���        ���,ʣ��༭�˵�δʵ��
 * 3.ʵ�ֶ���ʽ���� 		���
 * 4.ͼ�ν���ʵ�����ݶ���	���
 * 5.ʵ�ִ����׳�		�������
 * 6.ʵ��������ض���		���
 * 7.ʵ�ֶ���ʽ���Ź���	δ���
 * 8.ʵ���м�������		���
 * 9.���������Ż�		���
 * 10.��д�����ļ�		���read,write,if,while,declare,����ʽ���㣬�������
 */

public class CMMApp {

	private JFrame frmCmm;
	private JMenuBar menuBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CMMApp window = new CMMApp();
					
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					SwingUtilities.updateComponentTreeUI(window.frmCmm);
					window.frmCmm.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CMMApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCmm = new JFrame();
		frmCmm.setResizable(false);

		frmCmm.setTitle("CMM\u89E3\u91CA\u5668");
		frmCmm.setIconImage(Toolkit.getDefaultToolkit().getImage(CMMApp.class.getResource("/images/cmm.png")));
		frmCmm.setBounds(100, 100, 923, 703);
		frmCmm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCmm.getContentPane().setLayout(null);

		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 907, 21);
		frmCmm.getContentPane().add(menuBar);

		JMenu mnFilef = new JMenu("\u6587\u4EF6(F)");
		menuBar.add(mnFilef);

		JMenuItem mntmNewfile = new JMenuItem("\u65B0\u5EFA\u6587\u4EF6");
		mntmNewfile.setIcon(new ImageIcon(CMMApp.class.getResource("/images/new.png")));
		mnFilef.add(mntmNewfile);

		JMenuItem mntmOpenfile = new JMenuItem("\u6253\u5F00\u6587\u4EF6");
		mntmOpenfile.setIcon(new ImageIcon(CMMApp.class.getResource("/images/open.png")));
		mnFilef.add(mntmOpenfile);

		JMenuItem mntmSave = new JMenuItem("\u4FDD\u5B58\u6587\u4EF6");
		
		mntmSave.setIcon(new ImageIcon(CMMApp.class.getResource("/images/save.png")));
		mnFilef.add(mntmSave);

		JMenuItem mntmSet = new JMenuItem("\u8BBE\u7F6E");
		mntmSet.setIcon(new ImageIcon(CMMApp.class.getResource("/images/Settings.png")));
		mnFilef.add(mntmSet);

		JMenuItem mntmExit = new JMenuItem("\u9000\u51FA");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mntmExit.setIcon(new ImageIcon(CMMApp.class.getResource("/images/exit.png")));
		mnFilef.add(mntmExit);

		JMenu mnRun = new JMenu("\u8FD0\u884C(R)");
		menuBar.add(mnRun);

		JMenuItem mntmLexer = new JMenuItem("\u8BCD\u6CD5\u5206\u6790");
		
		mntmLexer.setIcon(new ImageIcon(CMMApp.class.getResource("/images/lex.png")));
		mnRun.add(mntmLexer);

		JMenuItem mntmParser = new JMenuItem("\u8BED\u6CD5\u5206\u6790");
		
		mntmParser.setIcon(new ImageIcon(CMMApp.class.getResource("/images/parse.png")));
		mnRun.add(mntmParser);
		
		JMenuItem mntmIntermediatecode = new JMenuItem("\u4E2D\u95F4\u4EE3\u7801");
		mntmIntermediatecode.setIcon(new ImageIcon(CMMApp.class.getResource("/images/G.png")));
		
		mnRun.add(mntmIntermediatecode);

		JMenuItem mntmRun = new JMenuItem("\u8FD0\u884C");
		
		mntmRun.setIcon(new ImageIcon(CMMApp.class.getResource("/images/run.png")));
		mnRun.add(mntmRun);

		JMenu mnHelp = new JMenu("\u5E2E\u52A9(H)");
		menuBar.add(mnHelp);

		JMenuItem mntmHelp = new JMenuItem("\u5E2E\u52A9");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmCmm, "�����﷨��ο�CMMʾ���ļ���");
			}
		});
		mntmHelp.setIcon(new ImageIcon(CMMApp.class.getResource("/images/help.png")));
		mnHelp.add(mntmHelp);

		JMenuItem mntmAbout = new JMenuItem("\u5173\u4E8E");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmCmm, "CMM������1.0");
				
			}
		});
		mntmAbout.setIcon(new ImageIcon(CMMApp.class.getResource("/images/about.png")));
		mnHelp.add(mntmAbout);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 46, 612, 402);
		frmCmm.getContentPane().add(scrollPane);

		// ---------------------------------------------------------------------
		JTextPane textPane = new JTextPane();
		textPane.setBorder(new LineNumber());
		Font font = new Font("Consolas", Font.BOLD, 14);
		textPane.setFont(font);
		textPane.getDocument().addDocumentListener(new SyntaxHighlighter(textPane));

		scrollPane.setViewportView(textPane);
		// ---------------------------------------------------------------------------
		// ������������
		// SimpleAttributeSet attributeSet=new SimpleAttributeSet();
		// StyleConstants.setFontSize(attributeSet, 16);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 458, 619, 196);
		frmCmm.getContentPane().add(tabbedPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Console", null, scrollPane_1, null);

		JTextArea textArea_Console = new JTextArea();
		textArea_Console.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				tabbedPane.setSelectedIndex(0);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		textArea_Console.setFont(font);
		textArea_Console.setEditable(false);
		scrollPane_1.setViewportView(textArea_Console);

		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPane.addTab("Error", null, scrollPane_2, null);

		JTextArea textArea_Error = new JTextArea();
		textArea_Error.setFont(font);
		textArea_Error.setEditable(false);
		textArea_Error.setText("");
		textArea_Error.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				tabbedPane.setSelectedIndex(1);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		scrollPane_2.setViewportView(textArea_Error);
		

		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(0, 20, 452, 21);
		frmCmm.getContentPane().add(toolBar);

		JButton btnNew = new JButton("");
		
		btnNew.setToolTipText("\u65B0\u5EFA");
		btnNew.setIcon(new ImageIcon(CMMApp.class.getResource("/images/new.png")));
		toolBar.add(btnNew);

		JButton btnOpen = new JButton("");
		btnOpen.setToolTipText("\u6253\u5F00");
		btnOpen.setIcon(new ImageIcon(CMMApp.class.getResource("/images/open.png")));
		toolBar.add(btnOpen);

		JButton btnSave = new JButton("");

		btnSave.setToolTipText("\u4FDD\u5B58");
		btnSave.setIcon(new ImageIcon(CMMApp.class.getResource("/images/save.png")));
		toolBar.add(btnSave);

		JButton btnLexer = new JButton("");

		btnLexer.setToolTipText("\u8BCD\u6CD5\u5206\u6790");
		btnLexer.setIcon(new ImageIcon(CMMApp.class.getResource("/images/lex.png")));
		toolBar.add(btnLexer);

		JButton btnParser = new JButton("");

		btnParser.setToolTipText("\u8BED\u6CD5\u5206\u6790");
		btnParser.setIcon(new ImageIcon(CMMApp.class.getResource("/images/parse.png")));
		toolBar.add(btnParser);

		JButton btnRun = new JButton("");

		btnRun.setToolTipText("\u8FD0\u884C");
		btnRun.setIcon(new ImageIcon(CMMApp.class.getResource("/images/run.png")));
		toolBar.add(btnRun);

		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_1.setBounds(632, 45, 265, 609);
		frmCmm.getContentPane().add(tabbedPane_1);

		JScrollPane scrollPane_3 = new JScrollPane();
		tabbedPane_1.addTab("\u8BCD\u6CD5\u5206\u6790", null, scrollPane_3, null);

		JTextArea textArea_Lexer_rs = new JTextArea();
		textArea_Lexer_rs.setFont(font);
		textArea_Lexer_rs.setEditable(false);
		scrollPane_3.setViewportView(textArea_Lexer_rs);

		JScrollPane scrollPane_4 = new JScrollPane();
		tabbedPane_1.addTab("\u8BED\u6CD5\u5206\u6790", null, scrollPane_4, null);

		JTextArea textArea_Parser_rs = new JTextArea();
		textArea_Parser_rs.setFont(font);
		textArea_Parser_rs.setEditable(false);
		scrollPane_4.setViewportView(textArea_Parser_rs);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		tabbedPane_1.addTab("\u4E2D\u95F4\u4EE3\u7801", null, scrollPane_5, null);
		
		JTextArea textArea_GenerCode_rs = new JTextArea();
		textArea_GenerCode_rs.setFont(font);
		scrollPane_5.setViewportView(textArea_GenerCode_rs);

		// ���ü���
		frmCmm.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				menuBar.setBounds(0, 0, frmCmm.getWidth(), 21);
			}
		});

		// �˵������ļ������¼�
		mntmOpenfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("���ļ�");
				// �����ļ�ѡ����
				JFileChooser fileChooser = new JFileChooser();
				// ���õ�ǰĿ¼
				fileChooser.setCurrentDirectory(new File("."));
				fileChooser.setAcceptAllFileFilterUsed(false);
				final String[][] fileENames = { { ".cmm", "CMMԴ���� �ļ�(*.cmm)" },
						// { ".doc", "MS-Word 2003 �ļ�(*.doc)" },
						// { ".xls", "MS-Excel 2003 �ļ�(*.xls)" }
				};

				// ��ʾ�����ļ�
				fileChooser.addChoosableFileFilter(new FileFilter() {
					public boolean accept(File file) {

						return true;
					}

					public String getDescription() {
						return "�����ļ�(*.*)";
					}
				});

				// ѭ�������Ҫ��ʾ���ļ�
				for (final String[] fileEName : fileENames) {
					fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
						public boolean accept(File file) {
							if (file.getName().endsWith(fileEName[0]) || file.isDirectory()) {
								return true;
							}
							return false;
						}

						public String getDescription() {

							return fileEName[1];
						}

					});
				}

				int option=fileChooser.showDialog(null, null);
				if (option==fileChooser.APPROVE_OPTION) {
					textPane.setText("");
					File cmmfile = fileChooser.getSelectedFile();
					if (cmmfile.isFile() && cmmfile.exists()) {
						try {
							InputStreamReader reader = new InputStreamReader(new FileInputStream(cmmfile));
							BufferedReader bufferedReader = new BufferedReader(reader);
							String lineText = null;
							while ((lineText = bufferedReader.readLine()) != null) {
								System.out.println(lineText);
								// textArea.append(lineText+"\n");
								Document document = textPane.getDocument();
								document.insertString(document.getLength(), lineText + "\n", null);

							}
							reader.close();
						} catch (IOException | BadLocationException e) {
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}
					}

				}else if (option==fileChooser.CANCEL_OPTION) {
					System.out.println("ȡ��");
				}else {
					//����
				}
				
			}
		});

		// ���������ļ�
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("���ļ�");
				// �����ļ�ѡ����
				JFileChooser fileChooser = new JFileChooser();
				// ���õ�ǰĿ¼
				fileChooser.setCurrentDirectory(new File("."));
				fileChooser.setAcceptAllFileFilterUsed(false);
				final String[][] fileENames = { { ".cmm", "CMMԴ���� �ļ�(*.cmm)" },
						// { ".doc", "MS-Word 2003 �ļ�(*.doc)" },
						// { ".xls", "MS-Excel 2003 �ļ�(*.xls)" }
				};

				// ��ʾ�����ļ�
				fileChooser.addChoosableFileFilter(new FileFilter() {
					public boolean accept(File file) {

						return true;
					}

					public String getDescription() {
						return "�����ļ�(*.*)";
					}
				});

				// ѭ�������Ҫ��ʾ���ļ�
				for (final String[] fileEName : fileENames) {
					fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
						public boolean accept(File file) {
							if (file.getName().endsWith(fileEName[0]) || file.isDirectory()) {
								return true;
							}
							return false;
						}

						public String getDescription() {

							return fileEName[1];
						}

					});
				}

				int option=fileChooser.showDialog(null, null);
				if (option==fileChooser.APPROVE_OPTION) {
					textPane.setText("");
					File cmmfile = fileChooser.getSelectedFile();
					if (cmmfile.isFile() && cmmfile.exists()) {
						try {
							InputStreamReader reader = new InputStreamReader(new FileInputStream(cmmfile));
							BufferedReader bufferedReader = new BufferedReader(reader);
							String lineText = null;
							while ((lineText = bufferedReader.readLine()) != null) {
								System.out.println(lineText);
								// textPane.append(lineText+"\n");
								Document document = textPane.getDocument();
								document.insertString(document.getLength(), lineText + "\n", null);
							}
							reader.close();
						} catch (IOException | BadLocationException e1) {
							// TODO �Զ����ɵ� catch ��
							e1.printStackTrace();
						}
					}
				}else if (option==fileChooser.CANCEL_OPTION) {
					System.out.println("ȡ��");
				}else {
					//���ִ���
				}
				

			}
		});
		// -------------------------------------------------------------------------
		// �˵��½��ļ������¼�
		mntmNewfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textPane.setText("");
			}
		});
		//-------------------------------------------------------------------------------
		//�������½��ļ�
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textPane.setText("");
			}
		});
		
		//-------------------------------------------------------------------------
		//�˵��������ļ�
		//
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				int option=fileChooser.showSaveDialog(null);
				if (option==fileChooser.APPROVE_OPTION) {
					String content=textPane.getText();
					File file=fileChooser.getSelectedFile();
					if (file.exists()) {
						//�ļ�����
						
						try {
							BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
							bufferedWriter.write(content);
							bufferedWriter.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}else {
						//�ļ�������
						try {
							file.createNewFile();
							BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
							bufferedWriter.write(content);
							bufferedWriter.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
					
					System.out.println(file.getAbsolutePath()+"--------"+file.getName());
					
				}else if (option==fileChooser.CANCEL_OPTION) {
					System.out.println("ȡ������");
				}else {
					//����
				}
			}
		});
		
		// -----------------------------------------------------------------------
		// �����������ļ�
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				int option=fileChooser.showSaveDialog(null);
				if (option==fileChooser.APPROVE_OPTION) {
					String content=textPane.getText();
					File file=fileChooser.getSelectedFile();
					if (file.exists()) {
						//�ļ�����
						
						try {
							BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
							bufferedWriter.write(content);
							bufferedWriter.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}else {
						//�ļ�������
						try {
							file.createNewFile();
							BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
							bufferedWriter.write(content);
							bufferedWriter.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
					
					System.out.println(file.getAbsolutePath()+"--------"+file.getName());
					
				}else if (option==fileChooser.CANCEL_OPTION) {
					System.out.println("ȡ������");
				}else {
					//����
				}
				
			}
		});
		
		/*
		 * ���в˵���
		 * 
		 */
		mntmRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//������ض���
				MyOutputStream myOutputStream=new MyOutputStream(System.out, textArea_Error);
				System.setErr(myOutputStream);
				myOutputStream.flush();
				
				String text = textPane.getText();
				textArea_Console.setText("");
				textArea_Error.setText("");
				if (!text.equals("") && text != null) {

					Reader in = new StringReader(text);
					BufferedReader bufferedReader = new BufferedReader(in);
					Lexer lexer = new Lexer();
					try {
						// �ʷ�����
						ArrayList<Token> list = lexer.checkType(bufferedReader);
						textArea_Lexer_rs.setText("");
						textArea_Lexer_rs.setText(lexer.printTokenList(list));
						// �﷨����
						Parser parser = new Parser(list);
						TreeNode root = parser.begainParse();
						textArea_Parser_rs.setText("");
						textArea_Parser_rs.setText(parser.printTree(root));

						// �������
						SemanticParser semanticParser = new SemanticParser();
						String result = semanticParser.parseTree(root);
						System.out.println("result:" + result);
						textArea_Console.setText(result);
					} catch (IOException | CmmException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}

				} else {
					// �༭��Ϊ��
				}
			}
		});
		/*
		 * -----------------------------------------------------------------
		 * ���а�ť
		 */
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//������ض���
				MyOutputStream myOutputStream=new MyOutputStream(System.out, textArea_Error);
				System.setErr(myOutputStream);
//				System.setOut(myOutputStream);
				myOutputStream.flush();
				textArea_Console.setText("");
				textArea_Error.setText("");
				String text = textPane.getText();

				if (!text.equals("") && text != null) {

					Reader in = new StringReader(text);
					BufferedReader bufferedReader = new BufferedReader(in);
					Lexer lexer = new Lexer();
					try {
						// �ʷ�����
						ArrayList<Token> list = lexer.checkType(bufferedReader);
						textArea_Lexer_rs.setText("");
						textArea_Lexer_rs.setText(lexer.printTokenList(list));
						// �﷨����
						Parser parser = new Parser(list);
						TreeNode root = parser.begainParse();
						textArea_Parser_rs.setText("");
						textArea_Parser_rs.setText(parser.printTree(root));

						// �������
						SemanticParser semanticParser = new SemanticParser();
						String result = semanticParser.parseTree(root);
						System.out.println("result:" + result);
						textArea_Console.setText(result);
						//�м����
						GenerateCode generateCode=new GenerateCode();
						generateCode.parseTree(root);
						textArea_GenerCode_rs.setText(generateCode.getGenerateText());
						
					} catch (IOException | CmmException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}

				} else {
					// �༭��Ϊ��
				}

			}
		});
		
		/*
		 * �ʷ������˵���
		 */
		mntmLexer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_Console.setText("");
				textArea_Error.setText("");
				String text = textPane.getText();
				if (!text.equals("") && text != null) {

					Reader in = new StringReader(text);
					BufferedReader bufferedReader = new BufferedReader(in);
					Lexer lexer = new Lexer();
					try {
						// �ʷ�����
						ArrayList<Token> list = lexer.checkType(bufferedReader);
						textArea_Lexer_rs.setText(lexer.printTokenList(list));

					} catch (IOException | CmmException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}

				} else {
					// �༭��Ϊ��
				}
			}
		});
		/*
		 * ---------------------------------------------------------- 
		 * �ʷ�������ť
		 */
		btnLexer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_Console.setText("");
				textArea_Error.setText("");
				String text = textPane.getText();
				if (!text.equals("") && text != null) {

					Reader in = new StringReader(text);
					BufferedReader bufferedReader = new BufferedReader(in);
					Lexer lexer = new Lexer();
					try {
						// �ʷ�����
						ArrayList<Token> list = lexer.checkType(bufferedReader);
						textArea_Lexer_rs.setText(lexer.printTokenList(list));

					} catch (IOException | CmmException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}

				} else {
					// �༭��Ϊ��
				}
			}
		});
		
		/*
		 * �﷨�����˵���
		 * 
		 */
		mntmParser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_Console.setText("");
				textArea_Error.setText("");
				String text = textPane.getText();
				if (!text.equals("") && text != null) {

					Reader in = new StringReader(text);
					BufferedReader bufferedReader = new BufferedReader(in);
					Lexer lexer = new Lexer();
					try {
						// �ʷ�����
						ArrayList<Token> list = lexer.checkType(bufferedReader);
						textArea_Lexer_rs.setText(lexer.printTokenList(list));
						// �﷨����
						Parser parser = new Parser(list);
						TreeNode root = parser.begainParse();
						textArea_Parser_rs.setText(parser.printTree(root));

					} catch (IOException | CmmException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}

				} else {
					// �༭��Ϊ��
				}
			}
		});
		/*
		 * -------------------------------------------------------------------
		 * �﷨������ť
		 */
		btnParser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_Console.setText("");
				textArea_Error.setText("");
				String text = textPane.getText();
				if (!text.equals("") && text != null) {

					Reader in = new StringReader(text);
					BufferedReader bufferedReader = new BufferedReader(in);
					Lexer lexer = new Lexer();
					try {
						// �ʷ�����
						ArrayList<Token> list = lexer.checkType(bufferedReader);
						textArea_Lexer_rs.setText(lexer.printTokenList(list));
						// �﷨����
						Parser parser = new Parser(list);
						TreeNode root = parser.begainParse();
						textArea_Parser_rs.setText(parser.printTree(root));

					} catch (IOException | CmmException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}

				} else {
					// �༭��Ϊ��
				}
			}
		});

		// ----------------------------
		//�����м����
		mntmIntermediatecode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//������ض���
				MyOutputStream myOutputStream=new MyOutputStream(System.out, textArea_Error);
				System.setErr(myOutputStream);
//				System.setOut(myOutputStream);
				myOutputStream.flush();
				textArea_Console.setText("");
				textArea_Error.setText("");
				String text = textPane.getText();

				if (!text.equals("") && text != null) {

					Reader in = new StringReader(text);
					BufferedReader bufferedReader = new BufferedReader(in);
					Lexer lexer = new Lexer();
					try {
						// �ʷ�����
						ArrayList<Token> list = lexer.checkType(bufferedReader);
						textArea_Lexer_rs.setText("");
						textArea_Lexer_rs.setText(lexer.printTokenList(list));
						// �﷨����
						Parser parser = new Parser(list);
						TreeNode root = parser.begainParse();
						textArea_Parser_rs.setText("");
						textArea_Parser_rs.setText(parser.printTree(root));
						//�м����
						GenerateCode generateCode=new GenerateCode();
						generateCode.parseTree(root);
						textArea_GenerCode_rs.setText(generateCode.getGenerateText());
						
					} catch (IOException | CmmException e1) {
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
					}

				} else {
					// �༭��Ϊ��
				}
			}
		});
		
	}
}
