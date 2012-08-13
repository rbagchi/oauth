package com.welocally.oauth.controller;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.welocally.oauth.service.SessionService;

@Controller
@Scope("request")
public class SpreadsheetController {
	@Autowired
	SessionService sessionService;

	@RequestMapping(value = "/hello")
	protected ModelAndView handleHello(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("HelloWorldPage");
		model.addObject("msg", "hello world");
		return model;
	}

	@Value("${foo}")
	String foo;
	@RequestMapping(value = "/props")
	protected ModelAndView handleProps(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("HelloWorldPage");
		model.addObject("msg", foo);
		return model;
	}
	@RequestMapping(value = "/spreadsheet", method = RequestMethod.GET)
	public ModelAndView handleSpreadsheetRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("EnterSpreadsheetName");
		return model;
	}

	@RequestMapping(value = "/readSpreadsheet", method = RequestMethod.GET)
	public String readSpreadsheetRequest(
			@RequestParam("spreadsheetName") String spreadsheetName,
			@RequestParam(value = "oauth_verifier", required = false) String oauth_verifier,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Token accessToken = sessionService.getAccessToken();
		if (accessToken == null) {
			String callbackURL = request.getRequestURL()
					.append("?spreadsheetName=").append(spreadsheetName)
					.toString();
			if (oauth_verifier == null) {
				String oAuthRedirectURL = sessionService
						.getOAuthorizationURL(callbackURL);
				return "redirect:" + oAuthRedirectURL;
			} else {
				sessionService.authorizeWith(oauth_verifier);
				return "redirect:" + callbackURL;
			}
		}

		String spreadsheetContent = sessionService
				.retrieveWorksheets(spreadsheetName);
		request.setAttribute("msg", spreadsheetContent);
		return "Debug";
	}

}
