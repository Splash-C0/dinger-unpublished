package com.facebook.accountkit.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.HttpCookie;

final class SerializableHttpCookie implements Serializable {
  private static final long serialVersionUID = 2064381394822046912L;
  private transient HttpCookie cookie;
  private Field fieldHttpOnly;

  public SerializableHttpCookie() {
  }

  public String encode(HttpCookie cookie) {
    this.cookie = cookie;
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    try {
      ObjectOutputStream outputStream = new ObjectOutputStream(os);
      outputStream.writeObject(this);
    } catch (IOException var4) {
      return null;
    }

    return this.byteArrayToHexString(os.toByteArray());
  }

  public HttpCookie decode(String encodedCookie) {
    byte[] bytes = this.hexStringToByteArray(encodedCookie);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    HttpCookie cookie = null;

    try {
      ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
      cookie = ((SerializableHttpCookie) objectInputStream.readObject()).cookie;
    } catch (ClassNotFoundException | IOException var6) {
    }

    return cookie;
  }

  private boolean getHttpOnly() {
    try {
      this.initFieldHttpOnly();
      return (Boolean) this.fieldHttpOnly.get(this.cookie);
    } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException var2) {
      return false;
    }
  }

  private void setHttpOnly(boolean httpOnly) {
    try {
      this.initFieldHttpOnly();
      this.fieldHttpOnly.set(this.cookie, httpOnly);
    } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException var3) {
    }

  }

  private void initFieldHttpOnly() throws NoSuchFieldException {
    this.fieldHttpOnly = this.cookie.getClass().getDeclaredField("httpOnly");
    this.fieldHttpOnly.setAccessible(true);
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(this.cookie.getName());
    out.writeObject(this.cookie.getValue());
    out.writeObject(this.cookie.getComment());
    out.writeObject(this.cookie.getCommentURL());
    out.writeObject(this.cookie.getDomain());
    out.writeLong(this.cookie.getMaxAge());
    out.writeObject(this.cookie.getPath());
    out.writeObject(this.cookie.getPortlist());
    out.writeInt(this.cookie.getVersion());
    out.writeBoolean(this.cookie.getSecure());
    out.writeBoolean(this.cookie.getDiscard());
    out.writeBoolean(this.getHttpOnly());
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    String name = (String) in.readObject();
    String value = (String) in.readObject();
    this.cookie = new HttpCookie(name, value);
    this.cookie.setComment((String) in.readObject());
    this.cookie.setCommentURL((String) in.readObject());
    this.cookie.setDomain((String) in.readObject());
    this.cookie.setMaxAge(in.readLong());
    this.cookie.setPath((String) in.readObject());
    this.cookie.setPortlist((String) in.readObject());
    this.cookie.setVersion(in.readInt());
    this.cookie.setSecure(in.readBoolean());
    this.cookie.setDiscard(in.readBoolean());
    this.setHttpOnly(in.readBoolean());
  }

  private String byteArrayToHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    byte[] var3 = bytes;
    int var4 = bytes.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      byte element = var3[var5];
      int v = element & 255;
      if (v < 16) {
        sb.append('0');
      }

      sb.append(Integer.toHexString(v));
    }

    return sb.toString();
  }

  private byte[] hexStringToByteArray(String hexString) {
    int len = hexString.length();
    byte[] data = new byte[len / 2];

    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
    }

    return data;
  }
}
