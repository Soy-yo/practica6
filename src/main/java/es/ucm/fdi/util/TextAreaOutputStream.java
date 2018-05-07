package es.ucm.fdi.util;

import javax.swing.*;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class TextAreaOutputStream extends OutputStream {

  private JTextArea textArea;

  public TextAreaOutputStream(JTextArea textArea) {
    this.textArea = textArea;
  }

  @Override
  public void write(int b) {
    textArea.append("" + b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws UnsupportedEncodingException {
    textArea.append(new String(b, off, len, "UTF-8"));
  }

  @Override
  public void write(byte[] b) throws UnsupportedEncodingException {
    textArea.append(new String(b, "UTF-8"));
  }

}
