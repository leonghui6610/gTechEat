package com.wheretoeat.gtech.controller;

import com.wheretoeat.gtech.dto.*;
import com.wheretoeat.gtech.service.EatGroupServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/eat")
public class EatGroupController {
    @Autowired
    public EatGroupServiceImpl eatGroupService;

    @GetMapping("/lunchGroup")
    public String showCreateGroupForm(Model m, HttpSession sess) {
        GroupInitResponse resp = (GroupInitResponse) sess.getAttribute("initResponse");
        m.addAttribute("formData", new CreateGroupFormData());
        JoinGroupFormData joinForm = new JoinGroupFormData();
        if (resp != null) {
            joinForm.setInviteCode(resp.getInviteCode());
        }
        m.addAttribute("joinFormData", joinForm);
        return "eat/lunchGroup";
    }

    @PostMapping("/lunchGroup")
    public String doCreateGroup(@Valid @ModelAttribute("formData") CreateGroupFormData formData,
                                BindingResult bindingResult,
                                Model model, HttpSession session) {
        GroupInitResponse startResponse = null;
        try {
            startResponse = eatGroupService.startGroup(formData.toParameters());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/eat/showError";
        }
        if (startResponse != null) {
            session.setAttribute("initResponse", startResponse);
            return "redirect:/eat/showGroup";
        }
        return "eat/lunchGroup";
    }

    @PostMapping("/joinGroup")
    public String doJoinGroup(@Valid @ModelAttribute("joinFormData") JoinGroupFormData joinFormData,
                              BindingResult bindingResult,
                              Model model, HttpSession session) {
        GroupInitResponse startResponse = null;
        /*if (bindingResult.hasErrors()) {
            return "eat/startGroup";
        }*/
        try {
            JoinGroupParam jparam = joinFormData.toParameters();
            startResponse = eatGroupService.inviteJoin(jparam.getInviteCode(), jparam.getUser());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/eat/showError";
        }
        if (startResponse != null) {
            session.setAttribute("initResponse", startResponse);
            return "redirect:/eat/showGroup";
        }
        return "eat/lunchGroup";
    }

    @GetMapping("/stop")
    public String stopGroup(Model model, HttpSession session) {
        GroupInitResponse resp = (GroupInitResponse) session.getAttribute("initResponse");
        try {
            eatGroupService.stopGroupSession(resp.getInviteCode(), resp.getSessId());
        } catch (Exception e) {
            log.error(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "/eat/showError";
        }
        return "/eat/showGroup";
    }


    @GetMapping("/showGroup")
    public String showGroupPage(Model model, HttpSession session) {
        GroupInitResponse resp = (GroupInitResponse) session.getAttribute("initResponse");
        //log.info("############showGroup initResponse {} {}", resp.getInviteCode(), resp.getSessId());
        if (resp == null) {
            log.error("session initResponse is null");
            model.addAttribute("error", "invalid session accessing");
            return "/eat/showError";
        }
        try {
            GroupResultResponse grpResult = eatGroupService.showPolling(resp.getInviteCode(), resp.getSessId());
            model.addAttribute("inviteCode", grpResult.getInviteCode());
            model.addAttribute("finalRestaurant", grpResult.getFinalRestaurant());
            model.addAttribute("users", grpResult.getLunchers());
            model.addAttribute("restaurants", grpResult.getRestaurants());
            model.addAttribute("sessId", resp.getSessId());
        } catch (Exception e) {
            log.error(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "/eat/showError";
        }
        return "/eat/showGroup"; //post to modify change restaurant
    }

    @PostMapping("/refreshUsersChoice") //api just for refreshing users/restaurants data fragment
    public String sendhtmlfragment(Model model, HttpSession session) {
        GroupInitResponse resp = (GroupInitResponse) session.getAttribute("initResponse");
        //log.info("############test_ajax_frag initResponse {} {}", resp.getInviteCode(), resp.getSessId());
        try {
            GroupResultResponse grpResult = eatGroupService.showPolling(resp.getInviteCode(), resp.getSessId());
            model.addAttribute("inviteCode", grpResult.getInviteCode());
            model.addAttribute("finalRestaurant", grpResult.getFinalRestaurant());
            model.addAttribute("users", grpResult.getLunchers());
            model.addAttribute("restaurants", grpResult.getRestaurants());
            model.addAttribute("sessId", resp.getSessId());
        } catch (Exception e) {
            log.error(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "/eat/showError";
        }
        return "/eat/showGroup :: refresh_frag";
    }

    @RequestMapping("/submitRestaurant")
    public String submitRestaurant(@RequestParam String restaurant, Model model, HttpSession sess) {
        boolean submitSuccess = false;
        GroupInitResponse resp = (GroupInitResponse) sess.getAttribute("initResponse");
        try {
            submitSuccess = eatGroupService.submitRestaurant(resp.getSessId(), restaurant);
        } catch (Exception e) {
            log.error(e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "/eat/showGroup";
    }

    @GetMapping("/showError")
    public String showError(Model m, HttpSession session) {
        return "eat/showError";
    }
}
