package com.polanco.updown.payload.response;
import java.util.Date; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
	
	String fileName;
	Date date;
	
	
}
