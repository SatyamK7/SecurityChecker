package com.satyam.SpringExtension;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow requests from your extension
public class SecurityAnalyzerController {


    private SecurityAnalyzerService analyzerService;

    public SecurityAnalyzerController(SecurityAnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping("/analyze")
    public SecurityReport analyzeWebsite(@RequestBody URLRequest request) {
        return analyzerService.analyze(request.getUrl());
    }
}

///////////////////////////////////AFTER AI INTEGRATION/////////////////////////////////
//@RestController
//@RequestMapping("/api/security")
//@CrossOrigin(origins = "*")
//public class SecurityAnalyzerController {
//
//    private final SecurityAnalyzerService analyzerService;
//
//    public SecurityAnalyzerController(SecurityAnalyzerService analyzerService) {
//        this.analyzerService = analyzerService;
//    }
//
//    @PostMapping("/analyze")
//    public Mono<String> analyzeCode(@RequestBody String input) {
//        return analyzerService.analyzeSecurity(input);
//    }
//}
