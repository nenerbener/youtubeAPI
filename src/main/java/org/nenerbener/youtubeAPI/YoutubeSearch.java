package org.nenerbener.youtubeAPI;

//Sample Java code for user authorization

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.*;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class YoutubeSearch {

 /** Application name. */
 private static final String APPLICATION_NAME = "Youtube Search";

 /** Directory to store user credentials for this application. */
 private static final java.io.File DATA_STORE_DIR = new java.io.File(
 System.getProperty("user.home"), ".credentials/java-youtube-api.youtube-key.txt");

 /** Global instance of the {@link FileDataStoreFactory}. */
 private static FileDataStoreFactory DATA_STORE_FACTORY;

 /** Global instance of the JSON factory. */
 private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

 /** Global instance of the HTTP transport. */
 private static HttpTransport HTTP_TRANSPORT;

 /** Global instance of the scopes required by this quickstart.
  *
  * If modifying these scopes, delete your previously saved credentials
  * at ~/.credentials/drive-java-quickstart
  */
// private static final Collection<String> SCOPES = Arrays.asList("YouTubeScopes.https://www.googleapis.com/auth/youtube.force-ssl YouTubeScopes.https://www.googleapis.com/auth/youtubepartner");
 private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl", "https://www.googleapis.com/auth/youtubepartner");

 static {
     try {
         HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
         DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
     } catch (Throwable t) {
         t.printStackTrace();
         System.exit(1);
     }
 }

 /**
  * Creates an authorized Credential object.
  * @return an authorized Credential object.
  * @throws IOException
  */
 public static Credential authorize() throws IOException {
// Load client secrets.
//	 String clientSecretFile = System.getProperty("user.home");
//	 clientSecretFile = new StringBuilder(clientSecretFile).append(
//			 "client_secret_320360237242-hen7ga6htg4j3g819m7dt3n8pf3v3ieq.apps.googleusercontent.com.json").toString();
	 String clientSecretFile = "googleCreds.txt";
	 Class<?> cls = null;
	 try {
		cls = Class.forName("org.nenerbener.youtubeAPI.YoutubeSearch");
	 } catch (ClassNotFoundException e) {
		 e.printStackTrace();
		 System.exit(-1);
	 }
//     InputStream in = YoutubeSearch.class.getResourceAsStream(clientSecretFile);
     InputStream in = cls.getResourceAsStream(clientSecretFile);
     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader( in ));

     // Build flow and trigger user authorization request.
     GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
     HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
         .setDataStoreFactory(DATA_STORE_FACTORY)
         .setAccessType("offline")
         .build();
     Credential credential = new AuthorizationCodeInstalledApp(
     flow, new LocalServerReceiver()).authorize("user");
     System.out.println(
         "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
     return credential;
 }

 /**
  * Build and return an authorized API client service, such as a YouTube
  * Data API client service.
  * @return an authorized API client service
  * @throws IOException
  */
 public static YouTube getYouTubeService() throws IOException {
     Credential credential = authorize();
     return new YouTube.Builder(
     HTTP_TRANSPORT, JSON_FACTORY, credential)
         .setApplicationName(APPLICATION_NAME)
         .build();
 }

 public static void main(String[] args) throws IOException {

     YouTube youtube = getYouTubeService();

     try {
         HashMap<String, String> parameters = new HashMap<>();
         parameters.put("part", "snippet");
         parameters.put("maxResults", "25");
         parameters.put("q", "surfing");
         parameters.put("type", "");

         YouTube.Search.List searchListByKeywordRequest = youtube.search().list(parameters.get("part").toString());
         if (parameters.containsKey("maxResults")) {
             searchListByKeywordRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
         }

         if (parameters.containsKey("q") && parameters.get("q") != "") {
             searchListByKeywordRequest.setQ(parameters.get("q").toString());
         }

         if (parameters.containsKey("type") && parameters.get("type") != "") {
             searchListByKeywordRequest.setType(parameters.get("type").toString());
         }

         SearchListResponse response = searchListByKeywordRequest.execute();
         System.out.println(response);


     } catch (GoogleJsonResponseException e) {
         e.printStackTrace();
         System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
     } catch (Throwable t) {
         t.printStackTrace();
     }
 }
}
