package com.ms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.vo.PageDataVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.LimitedDataBufferList;
import org.springframework.http.*;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Service
public class IntegrationService {

	@Value("${confluence.api.base.url}")
	private String baseUrl;

	@Value("${confluence.api.token}")
	private String apiToken;

	@Value("${confluence.api.username}")
	private String username;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * This method fetches all the data from the confluence app using the API Key.
	 * 1. You'll need to fetch all the pages in the Confluence space 2. Fetch all
	 * the contents of the pages and return it in JSON format
	 *
	 * Please ensure that the following points are taken care of: 1. Follow Java
	 * Coding & Naming standards 2. Do not hardcode any values, unless required 3.
	 * Add comments to your code 4. Do proper Exception handling
	 *
	 * @return List of Page with details
	 */

	public List<PageDataVO> fetchPages(String[] ids, Integer[] spaceIds, String[] status, String[] titles,
			Integer limit, String sort, String cursor) {

		String endUrl = "/wiki/api/v2/pages";

		boolean fetchFromCursor = false;

		boolean firstQueryParameter = false;

		// checking for each Request Param value for making final query

		if (ids != null) {
			boolean flag = false;
			endUrl += "?id=";
			firstQueryParameter = true;
			for (String i : ids) {
				flag = true;
				endUrl += i + ",";
			}
			if (flag)
				endUrl = endUrl.substring(0, endUrl.length() - 1);
		}
		if (spaceIds != null) {
			boolean flag = false;
			if (firstQueryParameter) {
				endUrl += "&space-id=";
			} else {
				endUrl += "?space-id=";
				firstQueryParameter = true;
			}
			for (int i : spaceIds) {
				endUrl += i + ",";
				flag = true;
			}
			if (flag)
				endUrl = endUrl.substring(0, endUrl.length() - 1);
		}
		if (status != null) {
			boolean flag = false;
			if (firstQueryParameter) {
				endUrl += "&status=";
			} else {
				endUrl += "?status=";
				firstQueryParameter = true;
			}
			for (String sts : status) {
				endUrl += sts + ",";
				flag = true;
			}
			if (flag)
				endUrl = endUrl.substring(0, endUrl.length() - 1);
		}
		if (titles != null) {
			boolean flag = false;
			if (firstQueryParameter) {
				endUrl += "&title=";
			} else {
				endUrl += "?title=";
				firstQueryParameter = true;
			}
			for (String title : titles) {
				endUrl += title + ",";
				flag = true;
			}
			if (flag)
				endUrl = endUrl.substring(0, endUrl.length() - 1);
		}
		if (limit != null) {
			if (firstQueryParameter) {
				endUrl += "&limit=" + limit;
			} else {
				endUrl += "?limit=" + limit;
				firstQueryParameter = true;
			}
		}
		if (sort != null && (sort.equals("id") || sort.equals("-id") || sort.equals("created-date")
				|| sort.equals("-created-date") || sort.equals("modified-date") || sort.equals("-modified-date")
				|| sort.equals("title") || sort.equals("-title"))) {
			if (firstQueryParameter) {
				endUrl += "&sort=" + sort;
			} else {
				endUrl += "?sort=" + sort;
				firstQueryParameter = true;
			}
		}
		if (cursor != null) {
			if (firstQueryParameter) {
				endUrl += "&cursor=" + cursor;
			} else {
				endUrl += "?cursor=" + cursor;
				firstQueryParameter = true;
			}
		}
		// It will return all pages of confluence since no request parameter passed
		if ((ids == null) && (spaceIds == null) && (status == null) && (titles == null) && (limit == null)
				&& (sort == null) && (cursor == null)) {
			fetchFromCursor = true;
			endUrl += "?limit=5";
		}

		// set Basic Authentication in Header
		List<PageDataVO> pages = new ArrayList<>();

		String apiUrl = baseUrl + endUrl;

		System.out.println("apiUrl:" + apiUrl);

		boolean flag = true;

		try {
			while (flag) {

				ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET,
						this.SetAuthenticationInHeader(), String.class);

				if (response.getStatusCode() == HttpStatus.OK) {
					JsonNode root = objectMapper.readTree(response.getBody());
					JsonNode results = root.path("results");
					for (JsonNode pageNode : results) {
						PageDataVO pageData = new PageDataVO();
						pageData.setId(pageNode.path("id").asText());
						pageData.setTitle(pageNode.path("title").asText());
						pageData.setStatus(pageNode.path("status").asText());
						pageData.setCreatedAt(pageNode.path("createdAt").asText());
						pageData.setAuthorId(pageNode.path("authorId").asText());
						pageData.setParentId(pageNode.path("parentId").asText());
						pageData.setSpaceId(pageNode.path("spaceId").asText());
						pageData.setBody(pageNode.path("body").asText());
						pages.add(pageData);
					}

					// Check for pagination
					JsonNode links = root.path("_links");
					JsonNode nextLink = links.path("next");
					if (fetchFromCursor && !nextLink.isMissingNode() && !nextLink.asText().isEmpty()) {
						apiUrl = baseUrl + nextLink.asText();
					} else {
						flag = false;
					}
				} else {

					throw new RuntimeException("Failed to fetch pages: " + response.getStatusCode());
				}
			}
		} catch (Exception e) {
			// Logging the exception
			System.err.println("Error fetching pages: " + e.getMessage());
			throw new RuntimeException("Error fetching pages", e);
		}

		return pages;

	}

	/**
	 * Search the pages for the content and return all the pages that contains it
	 *
	 * @return List of Page with details
	 */
	public List<PageDataVO> search(String searchString) {

		String end_url = "/wiki/rest/api/search?cql=text~\"" + searchString + "\"";
		String apiUrl = baseUrl + end_url;
		List<PageDataVO> pages = new ArrayList<>();
		System.out.println("searchUrl:" + apiUrl);

		try {
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET,
					this.SetAuthenticationInHeader(), String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				JsonNode root = objectMapper.readTree(response.getBody());
				JsonNode content = root.path("results");
				String ids[] = new String[content.size()];
				int index = 0;
				for (JsonNode pageNode : content) {
					PageDataVO pageData = new PageDataVO();
					String id = pageNode.path("content").path("id").asText();
					ids[index++] = id;
				}
				pages = fetchPages(ids, null, null, null, null, null, null);

			} else {
				throw new RuntimeException("Failed to fetch pages: " + response.getStatusCode());
			}

		} catch (Exception e) {
			System.out.println("Error while Fetching the result" + e);
		}

		return pages;

	}

	// setting up Basic Authentication in Header
	public HttpEntity<String> SetAuthenticationInHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, apiToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}
}
