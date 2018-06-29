package cn.xy.zb.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ConstService extends LogService{

	public Map<String, Boolean> map = new HashMap<String, Boolean>();
	
}
