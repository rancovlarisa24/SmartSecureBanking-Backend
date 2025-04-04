package com.lrs.SSB.controller;

import com.lrs.SSB.controller.VirtualCardDTO;
import com.lrs.SSB.entity.VirtualCard;
import com.lrs.SSB.service.VirtualCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/virtual-cards")
public class VirtualCardController {

    @Autowired
    private VirtualCardService virtualCardService;

    @PostMapping("/create")
    public ResponseEntity<VirtualCard> create(@RequestBody VirtualCardDTO dto) {
        VirtualCard card = virtualCardService.createVirtualCard(dto);
        return ResponseEntity.ok(card);
    }

}
