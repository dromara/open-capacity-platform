package com.open.capacity.file.context;


import com.open.capacity.common.disruptor.AsyncContext;

import lombok.Data;

@Data
public class UploadContext extends AsyncContext {
	
	private javax.servlet.AsyncContext asyncContext ;
	
}
