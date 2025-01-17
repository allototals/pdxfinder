package org.pdxfinder.services.email;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Created by abayomi on 16/08/2019.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail {

    private String senderMail;
    private String senderName;
    private String recipientMail;
    private String recipientName;
    private String subject;
    private List<Object> attachments;
    private Map<String, Object> model;
}
