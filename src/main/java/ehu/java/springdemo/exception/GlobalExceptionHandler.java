package ehu.java.springdemo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.thymeleaf.exceptions.TemplateInputException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public String handleNumberFormatException(NumberFormatException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("error", "Bad Request");
        model.addAttribute("message", "Invalid number format: " + ex.getMessage());
        model.addAttribute("path", "N/A");

        return "errors/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", "N/A");

        return "errors/error-500";
    }

    @ExceptionHandler(org.thymeleaf.exceptions.TemplateInputException.class)
    public String handleTemplateInputException(TemplateInputException ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Template Error");
        model.addAttribute("message", "An error occurred while processing the template: " + ex.getMessage());
        return "errors/error-500";
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", "Page not found: " + ex.getRequestURL());
        model.addAttribute("path", ex.getRequestURL());

        return "errors/error-404";
    }
}
