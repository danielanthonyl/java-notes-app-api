package com.notesapp.src.domain.DTOs.notes;

import java.util.UUID;

public class DeleteNoteByIdDTO {
   public UUID id; 

   public DeleteNoteByIdDTO(UUID id) {
    this.id = id;
   }
}
