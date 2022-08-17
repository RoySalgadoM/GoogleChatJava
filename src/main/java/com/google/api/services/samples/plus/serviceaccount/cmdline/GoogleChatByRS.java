/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.plus.serviceaccount.cmdline;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.chat.v1.HangoutsChat;
import com.google.api.services.chat.v1.model.*;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Roy Salgado
 */
public class GoogleChatByRS {

  private static final String APPLICATION_NAME = "sisaase87";
  
  private static final String SERVICE_ACCOUNT_EMAIL = "sisaase@sisaase-359218.iam.gserviceaccount.com";

  private static HttpTransport httpTransport;

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static HangoutsChat chat;

  private static final List<String> SCOPES = Arrays.asList(
          "https://www.googleapis.com/auth/chat.bot");
  public static void main(String[] args) {
    try {
      try {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // check for valid setup
        if (SERVICE_ACCOUNT_EMAIL.startsWith("Enter ")) {
          System.err.println(SERVICE_ACCOUNT_EMAIL);
          System.exit(1);
        }
        // service account credential (uncomment setServiceAccountUser for domain-wide delegation)
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
            .setServiceAccountScopes(SCOPES)
            .setServiceAccountPrivateKeyFromP12File(new File("sisaase.p12"))
            .build();
        HttpRequestInitializer httpRequestInitializer = credential;
        chat = new HangoutsChat(httpTransport,JSON_FACTORY,httpRequestInitializer);

        System.out.println(chat.spaces().list().execute());

        Message message = new Message();
        CardHeader cardHeader = new CardHeader();
        cardHeader.setTitle("SISA | Asesoría solicitada");
        cardHeader.setSubtitle("Recibiste esta notificación porque solicitaste una asesoría");
        cardHeader.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/Logo-utez.png/300px-Logo-utez.png");
        cardHeader.setImageStyle("IMAGE");


        List<WidgetMarkup> widgets = new ArrayList<WidgetMarkup>();

        WidgetMarkup materia = new WidgetMarkup();
        KeyValue materiaValue = new KeyValue();


//     Aprender de los formatos de las cartas   https://developers.google.com/chat/api/guides/message-formats/cards
        //materia
        materiaValue.setTopLabel("Materia:");
        materiaValue.setContent("Bases de datos");
        materia.setKeyValue(materiaValue);
        widgets.add(materia);

        //dia
        WidgetMarkup dia = new WidgetMarkup();
        KeyValue diaValue = new KeyValue();
        diaValue.setTopLabel("Día:");
        diaValue.setContent("22/08/2022");
        dia.setKeyValue(diaValue);
        widgets.add(dia);

        WidgetMarkup horario = new WidgetMarkup();
        KeyValue horarioValue = new KeyValue();
        horarioValue.setTopLabel("Horario:");
        horarioValue.setContent("13:00");
        horario.setKeyValue(horarioValue);
        widgets.add(horario);

        WidgetMarkup docente = new WidgetMarkup();
        KeyValue docenteValue = new KeyValue();
        docenteValue.setTopLabel("Docente:");
        docenteValue.setContent("Roy Axxel Salgado Martínez");
        docente.setKeyValue(docenteValue);
        widgets.add(docente);

        List<Section> sectionList = new ArrayList<Section>();

        Section dataSection = new Section();
        dataSection.setWidgets(widgets);


        //Boton final
        OpenLink openLink = new OpenLink();
        openLink.setUrl("https://srvcldutez.utez.edu.mx:8443/SISAVA/");
        OnClick onClick = new OnClick();
        onClick.setOpenLink(openLink);
        TextButton textButton = new TextButton();
        textButton.setText("Ir al SISA");
        textButton.setOnClick(onClick);
        Button button = new Button();
        button.setTextButton(textButton);
        WidgetMarkup buttonWidget = new WidgetMarkup();
        buttonWidget.setButtons(Collections.singletonList(button));
        Section buttonSection = new Section();
        buttonSection.setWidgets(Collections.singletonList(buttonWidget));

        sectionList.add(dataSection);
        sectionList.add(buttonSection);

        Card card = new Card();
        card.setHeader(cardHeader);
        card.setSections(sectionList);

        message.setCards(Collections.singletonList(card));
        chat.spaces().messages().create("spaces/AAAAftYThL4",message).execute();

      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

}