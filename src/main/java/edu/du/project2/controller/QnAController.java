package edu.du.project2.controller;

import edu.du.project2.dto.MessageDto;
import edu.du.project2.entity.QnAList;
import edu.du.project2.dto.AuthInfo;
import edu.du.project2.service.QnAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
@Slf4j
public class QnAController {

    private final QnAService qnAService;

    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "/common/messageRedirect";
    }

    @GetMapping("/1")
    public String index1(HttpSession session) {
        qnAService.initializeSession(session, 1L, "admin@test.com", "관리자", "admin");
        return "/qna/1";
    }

    @GetMapping("/2")
    public String index2(HttpSession session) {
        qnAService.initializeSession(session, 2L, "test@test.com", "테스터", "user");
        return "/qna/2";
    }

    @GetMapping("/inquiry")
    public String qna(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable, HttpSession session) {
        if (session.getAttribute("authInfo") == null) {
            MessageDto message = new MessageDto("로그인이 필요한 서비스입니다", "/", RequestMethod.GET, null);
            return showMessageAndRedirect(message, model);
        }
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        Page<QnAList> inquiries = qnAService.getInquiries(authInfo, pageable);
        model.addAttribute("inquirys", inquiries);
        return "/qna/inquiry";
    }

    @GetMapping("/inquiryDetail")
    public String qnaDetail(@RequestParam("id") Long id, Model model) {
        model.addAttribute("qna", qnAService.getInquiryDetail(id));
        model.addAttribute("lists", qnAService.getInquiryReplies(id));
        return "/qna/inquiryDetail";
    }

    @GetMapping("/inquiryInsertForm")
    public String qnaInsertForm() {
        return "/qna/inquiryInsertForm";
    }

    @PostMapping("/inquiryInsert")
    public String qnaInsert(@ModelAttribute QnAList list,
                            @RequestParam String content, HttpSession session) {
        qnAService.insertInquiry(list, content, session);
        return "redirect:/qna/inquiry";
    }

    @PostMapping("/answerInsert")
    public String answerInsert(@RequestParam String content,
                               @RequestParam String role,
                               @RequestParam Long id) {
        qnAService.insertAnswer(content, role, id);
        return "redirect:/qna/inquiryDetail?id=" + id;
    }
}
