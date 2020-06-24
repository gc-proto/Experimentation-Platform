package ca.gc.tbs.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import ca.gc.tbs.domain.OriginalProblem;
import ca.gc.tbs.domain.Problem;
import ca.gc.tbs.repository.OriginalProblemRepository;
import ca.gc.tbs.repository.ProblemRepository;
import ca.gc.tbs.service.ContentService;

@Controller
public class ProblemController {

	public static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("EEE MMM dd yyyy");
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger LOG = LoggerFactory.getLogger(ProblemController.class);
	public static final String COLLECTION_PROBLEM = "problem";

	@Autowired
	private ProblemRepository problemRepository;
	
	@Autowired
	private OriginalProblemRepository originalProblemRepository;

	@Autowired
	private ContentService contentService;

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/addProblem")
	public View addProblem(HttpServletRequest request) {

		try {
			String problemDetails = request.getParameter("problemDetails");
			problemDetails = this.contentService.cleanContent(problemDetails);
			Problem problem = new Problem(System.currentTimeMillis() + "", request.getParameter("url"),
					DATE_FORMAT.format(new Date()), request.getParameter("problem"), problemDetails, "Health Canada",
					request.getParameter("language"), "", "", "", "Test");
			problemRepository.save(problem);
			return new RedirectView("/dashboard");
		} catch (Exception e) {
			return new RedirectView("/error");
		}
	}

	@PostMapping(value = "/deleteTag")
	public @ResponseBody String deleteTag(HttpServletRequest request) {
		try {
			String tag = request.getParameter("tag");
			Optional<Problem> opt = problemRepository.findById(request.getParameter("id"));
			Problem problem = opt.get();
			problem.getTags().remove(tag);
			this.problemRepository.save(problem);
			return this.generateTagHtml(problem);
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@PostMapping(value = "/updateTags")
	public @ResponseBody String updateTags(HttpServletRequest request) {
		try {
			Optional<Problem> opt = problemRepository.findById(request.getParameter("id"));
			String tags[] = request.getParameter("tags").split(",");
			for (int i = 0; i < tags.length; i++) {
				tags[i] = tags[i].trim();
			}
			Problem problem = opt.get();
			problem.setTags(Arrays.asList(tags));
			this.problemRepository.save(problem);
			return this.generateTagHtml(problem);
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@PostMapping(value = "/updateProblem")
	public @ResponseBody String updateProblem(HttpServletRequest request) {
		try {
			Optional<Problem> opt = problemRepository.findById(request.getParameter("id"));
			Problem problem = opt.get();
			problem.setResolution(request.getParameter("resolution"));
			problem.setResolutionDate(DATE_FORMAT.format(new Date()));
			this.problemRepository.save(problem);
			return problem.getResolutionDate();
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@GetMapping(value = "/deleteProblem")
	public @ResponseBody String deleteProblem(HttpServletRequest request) {
		try {
			this.problemRepository.deleteById(request.getParameter("id"));
			return "deleted";
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	public String generateTagHtml(Problem problem) {
		StringBuilder builder = new StringBuilder();
		for (String tag : problem.getTags()) {
			builder.append("<button id='tagDelete" + problem.getId() + "' class='tagDeleteBtn btn btn-xs'>" + tag
					+ " (x)</button>");
		}
		return builder.toString();
	}

	public String getProblemData() {

		String returnData = "";
		try {
			StringBuilder builder = new StringBuilder();
			List<Problem> problems = this.problemRepository.findAll();
			for (Problem problem : problems) {
				builder.append("<tr><td>" + problem.getDepartment() + "</td>");
				builder.append("<td>" + problem.getLanguage() + "</td>");
				builder.append("<td>" + problem.getUrl() + "</td>");
				builder.append("<td>" + problem.getYesno() + "</td>");
				builder.append("<td>" + problem.getProblem() + "</td>");
				builder.append("<td>" + problem.getProblemDetails() + "</td>");
				builder.append("<td>" + DATE_FORMAT.format(INPUT_FORMAT.parse(problem.getProblemDate())) + "</td>");
				builder.append("<td class='tagCol'>");
				builder.append(this.generateTagHtml(problem));
				builder.append("</td>");
				try {
					builder.append("<td>" + problem.getResolution() + "</td>");
					builder.append("<td>" + problem.getResolutionDate() + "</td>");
				} catch (Exception e) {
					builder.append("<td></td>");
					builder.append("<td></td>");
				}
				builder.append("<td><div class='btn-group'><button id='tag" + problem.getId()
						+ "' class='btn btn-xs tagBtn'>Tag</button><button id='resolve" + problem.getId()
						+ "' class='btn btn-xs resolveBtn'>Resolve</button><button id='delete" + problem.getId()
						+ "' class='btn btn-xs deleteBtn'><span class='fas fa-trash-alt'></span><span class='wb-inv'>Delete</span></button></div></td>");
				builder.append("</tr>");
			}
			returnData = builder.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return returnData;
	}
	
	public String getBackupData() {

		String returnData = "";
		try {
			StringBuilder builder = new StringBuilder();
			List<OriginalProblem> problems = this.originalProblemRepository.findAll();
			for (Problem problem : problems) {
				builder.append("<tr>");
				builder.append("<td>" + problem.getLanguage() + "</td>");
				builder.append("<td>" + problem.getUrl() + "</td>");
				builder.append("<td>" + problem.getYesno() + "</td>");
				builder.append("<td>" + problem.getProblem() + "</td>");
				builder.append("<td>" + problem.getProblemDetails() + "</td>");
				builder.append("<td>" + DATE_FORMAT.format(INPUT_FORMAT.parse(problem.getProblemDate())) + "</td>");
				builder.append("</tr>");
			}
			returnData = builder.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return returnData;
	}

	@GetMapping(value = "/problemDashboard")
	public ModelAndView problemDashboard() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject("data", this.getProblemData());
		mav.setViewName("problemDashboard");
		return mav;
	}
	
	@GetMapping(value = "/backupDashboard")
	public ModelAndView backupDashboard() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject("data", this.getBackupData());
		mav.setViewName("backupDashboard");
		return mav;
	}

	@GetMapping(value = "/testForm")
	public String testForm() {
		return "testForm";
	}
}
