package com.forMyCV.tinylink.rateLimit.services;

import com.forMyCV.tinylink.response.Response;

public interface RateLimitService {

    public Response<Boolean> isAllowed(String clientIp);
//    public Response<Integer>getRemainingRequests(String clientIp);

}
