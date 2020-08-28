package com.facebook.accountkit.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class AccountKitCookieStore implements CookieStore {
  private static final String SP_COOKIE_STORE = "cookieStore";
  private static final String SP_KEY_DELIMITER = "|";
  private static final String SP_KEY_DELIMITER_REGEX = "\\|";
  private static final List<String> ALLOW_PERSIST_DOMAINS = new ArrayList();
  private static final List<String> ALLOW_PERSIST_COOKIE_NAMES = new ArrayList();

  static {
    ALLOW_PERSIST_DOMAINS.add(".accountkit.com");
    ALLOW_PERSIST_COOKIE_NAMES.add("aksb");
  }

  private final Map<URI, List<HttpCookie>> map = new HashMap();
  private final SharedPreferences sharedPreferences;

  public AccountKitCookieStore(Context context) {
    this.sharedPreferences = context.getSharedPreferences("cookieStore", 0);
    this.loadFromSharedPreferences();
  }

  private void loadFromSharedPreferences() {
    Map<String, ?> allPairs = this.sharedPreferences.getAll();
    Iterator var2 = allPairs.entrySet().iterator();

    while (var2.hasNext()) {
      Entry<String, ?> entry = (Entry) var2.next();
      String[] uriAndName = ((String) entry.getKey()).split("\\|", 2);

      try {
        URI uri = new URI(uriAndName[0]);
        String encodedCookie = (String) entry.getValue();
        HttpCookie cookie = (new SerializableHttpCookie()).decode(encodedCookie);
        if (cookie != null) {
          List<HttpCookie> targetCookies = (List) this.map.get(uri);
          if (targetCookies == null) {
            targetCookies = new ArrayList();
            this.map.put(uri, targetCookies);
          }

          ((List) targetCookies).add(cookie);
        }
      } catch (URISyntaxException var9) {
      }
    }

  }

  private void saveToSharedPreferences(URI uri, HttpCookie cookie) {
    if (ALLOW_PERSIST_DOMAINS.contains(cookie.getDomain()) && ALLOW_PERSIST_COOKIE_NAMES.contains(cookie.getName())) {
      Editor editor = this.sharedPreferences.edit();
      editor.putString(uri.toString() + "|" + cookie.getName(), (new SerializableHttpCookie()).encode(cookie));
      editor.apply();
    }

  }

  public synchronized void add(URI uri, HttpCookie cookie) {
    if (cookie == null) {
      throw new NullPointerException("cookie == null");
    } else {
      uri = this.cookiesUri(uri);
      List<HttpCookie> cookies = (List) this.map.get(uri);
      if (cookies == null) {
        cookies = new ArrayList();
        this.map.put(uri, cookies);
      } else {
        ((List) cookies).remove(cookie);
      }

      ((List) cookies).add(cookie);
      this.saveToSharedPreferences(uri, cookie);
    }
  }

  private URI cookiesUri(URI uri) {
    if (uri == null) {
      return null;
    } else {
      try {
        return new URI("http", uri.getHost(), (String) null, (String) null);
      } catch (URISyntaxException var3) {
        return uri;
      }
    }
  }

  public synchronized List<HttpCookie> get(URI uri) {
    if (uri == null) {
      throw new NullPointerException("uri == null");
    }

    List<HttpCookie> result = new ArrayList<>();


    List<HttpCookie> cookiesForUri = this.map.get(uri);
    if (cookiesForUri != null) {
      for (Iterator<HttpCookie> i = cookiesForUri.iterator(); i.hasNext(); ) {
        HttpCookie cookie = i.next();
        if (cookie.hasExpired()) {
          i.remove();
          continue;
        }
        result.add(cookie);
      }
    }


    for (Map.Entry<URI, List<HttpCookie>> entry : this.map.entrySet()) {
      if (uri.equals(entry.getKey())) {
        continue;
      }

      List<HttpCookie> entryCookies = entry.getValue();
      for (Iterator<HttpCookie> i = entryCookies.iterator(); i.hasNext(); ) {
        HttpCookie cookie = i.next();
        if (!HttpCookie.domainMatches(cookie.getDomain(), uri.getHost())) {
          continue;
        }
        if (cookie.hasExpired()) {
          i.remove();
          continue;
        }
        if (!result.contains(cookie)) {
          result.add(cookie);
        }
      }
    }

    return Collections.unmodifiableList(result);
  }

  public synchronized List<HttpCookie> getCookies() {
    List<HttpCookie> result = new ArrayList();
    Iterator var2 = this.map.values().iterator();

    while (var2.hasNext()) {
      List<HttpCookie> list = (List) var2.next();
      Iterator i = list.iterator();

      while (i.hasNext()) {
        HttpCookie cookie = (HttpCookie) i.next();
        if (cookie.hasExpired()) {
          i.remove();
        } else if (!result.contains(cookie)) {
          result.add(cookie);
        }
      }
    }

    return Collections.unmodifiableList(result);
  }

  public synchronized List<URI> getURIs() {
    List<URI> result = new ArrayList(this.map.keySet());
    result.remove((Object) null);
    return Collections.unmodifiableList(result);
  }

  public synchronized boolean remove(URI uri, HttpCookie cookie) {
    if (cookie == null) {
      throw new NullPointerException("cookie == null");
    } else {
      List<HttpCookie> cookies = (List) this.map.get(this.cookiesUri(uri));
      return cookies != null ? cookies.remove(cookie) : false;
    }
  }

  public synchronized boolean removeAll() {
    boolean result = !this.map.isEmpty();
    this.map.clear();
    return result;
  }
}
