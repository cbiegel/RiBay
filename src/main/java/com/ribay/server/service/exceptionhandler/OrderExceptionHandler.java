package com.ribay.server.service.exceptionhandler;

import com.ribay.server.exception.*;
import com.ribay.server.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by CD on 09.07.2016.
 */
@ControllerAdvice(assignableTypes = OrderService.class)
public class OrderExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler({EmptyCartException.class, NotLoggedInException.class, IncorrectHashException.class, IncorrectClientException.class, IncorrectUserException.class, IncompleteOrderException.class})
    public ModelAndView handlePreconditionFailed(HttpServletRequest req, Exception e) {
        return handleDefault(req, e);
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler({OrderTooOldException.class})
    public ModelAndView handleGone(HttpServletRequest req, Exception e) {
        return handleDefault(req, e);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({CartChangedException.class})
    public ModelAndView handleCartChanged(HttpServletRequest req, CartChangedException e) {
        ModelAndView mav = handleDefault(req, e); // like default handler ...
        mav.addObject("newOrder", e.getNewOrder()); // ... but also add new order so client can confirm new order
        return mav;
    }

    private ModelAndView handleDefault(HttpServletRequest req, Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }

}
