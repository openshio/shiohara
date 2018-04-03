package com.viglet.shiohara.api;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class ShAPI {

	@Autowired
	ShAPIBean shAPIBean;

	@RequestMapping(method = RequestMethod.GET)
	public ShAPIBean shApiInfo() throws JSONException {

		shAPIBean.setProduct("Viglet Shiohara");

		return shAPIBean;
	}
}
