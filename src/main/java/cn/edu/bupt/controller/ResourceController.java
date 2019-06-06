package cn.edu.bupt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Date;

@Controller
@RequestMapping("resources")
@Slf4j
public class ResourceController {
    @GetMapping("pdf/{pdfName}")
    public ResponseEntity<FileSystemResource> getPdf(@PathVariable String pdfName){

        HttpHeaders headers = new HttpHeaders();

        headers.add("Cache-Control", "max-age=0");
        headers.add("Content-Disposition", "inline; filename=" + pdfName);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        try {
            pdfName += ".pdf";
            File pdfFile = new File(this.getClass().getResource("/pdf/" + pdfName).getFile());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfFile.length())
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new FileSystemResource(pdfFile));
        }
        catch (NullPointerException e){
            log.debug("file not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
