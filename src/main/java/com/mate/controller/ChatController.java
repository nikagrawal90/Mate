package com.mate.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@CrossOrigin("*")
public class ChatController {
    // For now, we will use whatsapp for chatting.
    // We will allow host to create a Wapp group for each event programmatically using a single button on our site
}
