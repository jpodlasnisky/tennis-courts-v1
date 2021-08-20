package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("guest")
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @ApiOperation(value = "Create a new guest")
    @ApiResponse(code = 201, message = "A new guest has been created.")
    @PostMapping()
    public ResponseEntity<Void> createGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.addGuest(guestDTO).getId())).build();
    }

    @ApiOperation(value = "Find a guest by id")
    @ApiResponse(code = 200, message = "")
    @GetMapping("/{id}")
    public ResponseEntity<GuestDTO> findGuestById(@PathVariable(value = "id") Long guestId) {
        return ResponseEntity.ok(guestService.findGuestById(guestId));
    }

    @ApiOperation(value = "List all guests")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping()
    public ResponseEntity<List<GuestDTO>> listAllGuests() {
        return ResponseEntity.ok(guestService.listAllGuests());
    }

    @ApiOperation(value = "Find a guest by its name")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(params = "guestName")
    public ResponseEntity<Guest> findGuestByName(String guestName) {
        return ResponseEntity.ok(guestService.findGuestByName(guestName));
    }

    @ApiOperation(value = "Delete a guest by its id")
    @ApiResponse(code = 200, message = "Guest deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGuest(@PathVariable(value = "id") Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.ok("Guest deleted successfully.");
    }

    @ApiOperation(value = "Update a guest")
    @ApiResponse(code = 200, message = "Success")
    @PutMapping()
    public ResponseEntity<GuestDTO> updateGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.updateGuest(guestDTO));
    }

}
