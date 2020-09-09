package com.nutn.utm.controller;

import com.nutn.utm.exception.InvalidRequestException;
import com.nutn.utm.model.dto.form.FlightPlanApplicationForm;
import com.nutn.utm.model.dto.geojson.flightplan.FlightPlanFeatureCollectionDto;
import com.nutn.utm.model.dto.geojson.flightplan.FlightPlanFeatureDto;
import com.nutn.utm.model.dto.response.FlightPlanApplicationResponseDto;
import com.nutn.utm.model.entity.FlightPlan;
import com.nutn.utm.service.FlightPlanService;
import com.nutn.utm.utility.geojson.GeoJsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * @author swshawnwu@gmail.com(ShawnWu)
 */

@CrossOrigin
@RestController
@RequestMapping("/flightPlan")
public class FlightPlanController {

    GeoJsonConverter geoJsonConverter = new GeoJsonConverter();

    @Autowired
    FlightPlanService flightPlanService;

    @GetMapping("/{pilotAccount}/{date}")
    public FlightPlanFeatureCollectionDto getAllFlightPlansByDate(@PathVariable String pilotAccount, @PathVariable String date) {
        List<FlightPlan> flightPlans = flightPlanService.getAllFlightPlansByDate(pilotAccount, date);
        return geoJsonConverter.convertFlightPlansToFeatureCollection(flightPlans);
    }

    @GetMapping("/{pilotAccount}/{date}/{planId}")
    public FlightPlanFeatureDto getFlightPlanByDateAndId(@PathVariable String pilotAccount, @PathVariable String date, @PathVariable long planId) {
        FlightPlan flightPlans = flightPlanService.getFlightPlanByPlanId(pilotAccount, date, planId);
        return geoJsonConverter.convertFlightPlanToFeature(flightPlans);
    }

    @PostMapping
    public ResponseEntity<FlightPlanApplicationResponseDto> applyFlightPlan(@Valid @RequestBody FlightPlanApplicationForm planApplicationForm, BindingResult formFormatValidateResult) {
        if (formFormatValidateResult.hasErrors())
            throw new InvalidRequestException(formFormatValidateResult.getFieldErrors());
        return ResponseEntity.ok(new FlightPlanApplicationResponseDto(flightPlanService.applyFlightPlan(planApplicationForm)));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<FlightPlanApplicationResponseDto> modifyFlightPlan(@RequestBody FlightPlanApplicationForm modifiedPlanForm, @PathVariable long planId) {
        FlightPlan modifiedFlightPlan = flightPlanService.modifyFlightPlan(planId, modifiedPlanForm);
        return ResponseEntity.ok(new FlightPlanApplicationResponseDto(modifiedFlightPlan));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Long> deleteFlightPlan(@PathVariable long planId) {
        flightPlanService.deleteFlightPlan(planId);
        return ResponseEntity.ok(planId);
    }

}