package ru.inno.projects.controllers;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.inno.projects.models.Event;
import ru.inno.projects.models.Invitation;
import ru.inno.projects.models.Member;
import ru.inno.projects.models.User;
import ru.inno.projects.services.EventService;
import ru.inno.projects.services.InvitationService;
import ru.inno.projects.services.UserService;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/invitations")
public class InvitationController {

    private final EventService eventService;
    private final UserService userService;
    private final InvitationService invitationService;


    @Autowired
    public InvitationController(EventService eventService, UserService userService, InvitationService invitationService) {
        this.eventService = eventService;
        this.userService = userService;
        this.invitationService = invitationService;
    }

//    @PreAuthorize("hasAuthority('USER')")
//    @GetMapping
//    public String eventList(Model model) {
//        log.info("Start method eventList from EventController");
//        model.addAttribute("events", eventService.getAllEvents());
//        return "eventList";
//    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user")
    public String invitationUserList(@AuthenticationPrincipal User user, Model model) {
        log.info("Start method invitationUserList from InvitationController");
        List<Invitation> invitationsByInvitedUser = invitationService.getInvitationsByInvitedUser(user);
        for (Invitation invitation:
             invitationsByInvitedUser) {
            invitationService.markInvitationRead(invitation);
        }
        model.addAttribute("invitationsInvited", invitationsByInvitedUser);
        model.addAttribute("invitationsInvitor", invitationService.getInvitationsByInvitorUser(user));

//        model.addAttribute("username", user.getUsername());

        return "currentUserInvitations";
    }

    @PostMapping("/mark_invitation_accepted")
    public String markInvitationAccepted(@AuthenticationPrincipal User user,
                            @RequestParam("invitationId") Invitation invitation,
                            Model model) throws ParseException {
        log.info("Start method markInvitationAccepted");
        if(user.getUserId().equals(invitation.getInvitedUser().getUserId())) {
            invitationService.markInvitationAccepted(invitation);
        }
        return "redirect:/invitations/user";
    }

    @PostMapping("/mark_invitation_unaccepted")
    public String markInvitationUnAccepted(@AuthenticationPrincipal User user,
                            @RequestParam("invitationId") Invitation invitation,
                            Model model) throws ParseException {
        log.info("Start method markInvitationUnAccepted");
        if(user.getUserId().equals(invitation.getInvitedUser().getUserId())) {
            invitationService.markInvitationUnAccepted(invitation);
        }
        return "redirect:/invitations/user";
    }

}
