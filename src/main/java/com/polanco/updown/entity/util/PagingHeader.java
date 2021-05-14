package com.polanco.updown.entity.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PagingHeader {

	PAGE_SIZE("Page-Size"),
	PAGE_NUMBER("Page-Number"),
	PAGE_OFFSET("Page-Offset"),
	PAGE_TOTAL("Page-Total"),
	COUNT("Count");

	private final String name;
}
