package com.ms.api;

import com.ms.service.IntegrationService;
import com.ms.vo.PageDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IntegrationController {

    @Autowired
    private IntegrationService integrationService;

    /**
     *  Fetch pages and it's content
     */
    @GetMapping("/fetchPages")
    public List<PageDataVO> fetchPages(@Nullable @RequestParam("id") String[] ids, @Nullable @RequestParam("space-id") Integer[] space_ids, 
    		@Nullable @RequestParam("status") String[] status, @Nullable @RequestParam("title") String[] titles, 
    		@Nullable @ RequestParam("limit") Integer limit, @Nullable @RequestParam("sort") String sort, 
    		@Nullable @RequestParam("cursor") String cursor) {
    	
    		return integrationService.fetchPages(ids, space_ids, status, titles, limit, sort, cursor);
    }

    /**
     *  Fetch pages that contains the {searchString}
     */
    @GetMapping("/search/{searchString}")
    public List<PageDataVO> search(@PathVariable String searchString) {
        return integrationService.search(searchString);
    }
}
