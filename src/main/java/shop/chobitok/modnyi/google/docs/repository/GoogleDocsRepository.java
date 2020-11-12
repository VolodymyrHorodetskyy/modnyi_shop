package shop.chobitok.modnyi.google.docs.repository;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.*;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDocsRepository {

    private final String APPLICATION_NAME = "Google Docs API Java Quickstart";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final String TOKENS_DIRECTORY_PATH = "tokens";
    //   private static final String DOCUMENT_ID = "10VxMpSpo54mF1x1PbjEHl7UgHm6Kh5NwTCKB9P9TG5U";

    private final List<String> SCOPES = Collections.singletonList(DocsScopes.DOCUMENTS);
    private final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private Docs docs;


    public void updateDocumentByText(String documentId, String text) {
        removeWholeText(documentId);
        updateDoc(documentId, text);
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleDocsRepository.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8887).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


    private Docs getOrCreateDocs() {
        if (docs != null) {
            return docs;
        }
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            docs = new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            return docs;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return docs;
    }

    private void updateDoc(String documentId, String text) {
        Request request = new Request();
        request.setInsertText(new InsertTextRequest().setText(text).setLocation(new Location().setIndex(1)));
        BatchUpdateDocumentRequest batchUpdateDocumentRequest = new BatchUpdateDocumentRequest();
        batchUpdateDocumentRequest.setRequests(Arrays.asList(request));
        try {
            getOrCreateDocs().documents().batchUpdate(documentId, batchUpdateDocumentRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeWholeText(String documentId) {
        Integer endIndex = findEndRange(documentId) - 1;
        if (endIndex != null && endIndex > 1) {
            List<Request> requests = new ArrayList<>();
            requests.add(new Request().setDeleteContentRange(
                    new DeleteContentRangeRequest()
                            .setRange(new Range()
                                    .setStartIndex(1)
                                    .setEndIndex(findEndRange(documentId) - 1))
            ));

            BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
            try {
                getOrCreateDocs().documents()
                        .batchUpdate(documentId, body).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Integer findEndRange(String documentId) {
        Document response = null;
        try {
            response = getOrCreateDocs().documents().get(documentId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<StructuralElement> content = response.getBody().getContent();
        return content.get(content.size() - 1).getEndIndex();
    }

}
