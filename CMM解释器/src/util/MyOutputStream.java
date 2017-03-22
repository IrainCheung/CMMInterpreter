package util;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class MyOutputStream extends PrintStream{

	private JTextComponent textComponent;
	private StringBuffer sb=new StringBuffer();
	public MyOutputStream(OutputStream outputStream,JTextComponent textComponent) {
		
		super(outputStream);
		this.textComponent=textComponent;
	}
	
	
	

	@Override
	public void write(byte[] buf, int off, int len) {
		final String message=new String(buf, off, len);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				sb.append(message);
				textComponent.setText(sb.toString());
				
			}
		});
	}

}
