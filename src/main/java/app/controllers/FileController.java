package app.controllers;

import app.models.FileAttachment;
import app.models.Project;
import app.models.repository.FileAttachmentRepository;
import app.models.repository.ProjectRepository;
import app.storage.StorageFileNotFoundException;
import app.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static app.models.FileAttachment.FileAttachmentType;

@Controller
public class FileController {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/project/{id}/upload_proposal")
    public String uploadProposal(@PathVariable Long id,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        Project project = projectRepository.findOne(id);
        FileAttachment proposal = project.getProposal();
        if (proposal != null)
            uploadFile(file, proposal);
        else
            uploadFile(file, project, FileAttachmentType.PROPOSAL);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/student";
    }

    @PostMapping("/project/{id}/upload_final_report")
    public String uploadReport(@PathVariable Long id,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        Project project = projectRepository.findOne(id);
        FileAttachment report = project.getReport();
        if (report != null)
            uploadFile(file, report);
        else
            uploadFile(file, project, FileAttachmentType.FINAL_REPORT);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/student";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    private void uploadFile(MultipartFile uploadedFile, FileAttachment oldFile) {
        uploadFile(uploadedFile, oldFile.getProject(), oldFile.getProjectAssetType());
        fileAttachmentRepository.delete(oldFile.getId());
        String filename = getKey(oldFile.getProject(), oldFile.getProjectAssetType(), oldFile.getAssetUrl());
        storageService.delete(filename);
    }

    private void uploadFile(MultipartFile uploadedFile, Project project, FileAttachmentType fileType) {
        String filename = getKey(project, fileType, uploadedFile.getOriginalFilename());
        storageService.store(filename, uploadedFile);
        fileAttachmentRepository.save(
                new FileAttachment(filename, fileType, project)
        );
    }

    private String getKey(Project project, FileAttachmentType fileType, String filename) {
        return project.getDescription() + "-" + fileType + "-" + filename;
    }
}
