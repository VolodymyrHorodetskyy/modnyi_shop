package shop.chobitok.modnyi.aspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.service.MailService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MailService mailService;

    public void handleConflict(HttpServletRequest request, Throwable throwable) {
        if (!(throwable instanceof ConflictException)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Request URI: ").append(request.getRequestURI()).append("\n\n");
            stringBuilder.append("Message: ").append(throwable.getMessage()).append("\n\n");
            stringBuilder.append("Stack trace:").append("\n\n");
            stringBuilder.append(String.join("\n",
                    Arrays.stream(throwable.getStackTrace()).map(s -> s.toString()).collect(toList())));
            mailService.sendEmail("Exception happened", stringBuilder.toString(), "horodetskyyv@gmail.com");
        }
    }

}
