package com.yetanotherdevblog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html
 */
public class RestTemplateTest {

    private RestTemplate restTemplate = new RestTemplate();

    // exchange GET
    // exchange POST
    // exchange PUT
    // exchange DELETE

    // getForEntity
    // putForEntity
    // blah blah


    // How to get Status
    // How to get Body
    // How to get Headers
    @Test
    @Ignore
    public void test_GetStatus() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://example.com", String.class);
        HttpStatus status = response.getStatusCode();
        Assert.isTrue(status == HttpStatus.OK);
    }

    @Test
    @Ignore
    public void test_WorkWithListOfThings() {
        ParameterizedTypeReference<List<String>> listOfStrings = new ParameterizedTypeReference<List<String>>() {};
        ResponseEntity<List<String>> response
                = restTemplate.exchange("http://example.com",HttpMethod.GET,null,listOfStrings);
        HttpStatus status = response.getStatusCode();
        Assert.isTrue(status == HttpStatus.OK);
    }

    @Test
    @Ignore
    public void test_GetHeaders() {
        ParameterizedTypeReference<List<String>> listOfString = new ParameterizedTypeReference<List<String>>() {};
        ResponseEntity<List<String>> response= restTemplate.exchange("http://example.com",HttpMethod.GET,null, listOfString);
        HttpStatus status = response.getStatusCode();
        Assert.isTrue(status == HttpStatus.OK);

        Assert.notNull(response.getHeaders());
        Assert.notNull(response.getHeaders().getOrigin());
        Assert.notNull(response.getHeaders().getOrDefault("someHeaderKey", Collections.singletonList("test")));
    }

    // 2nd blog post
    // Get Headers
    // follow redirects
    // unfollow redirects

    @Test
    @Ignore
    public void test_SendSpecificHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "Header-Value");
        HttpEntity request = new HttpEntity(headers);

        ParameterizedTypeReference<List<String>> listOfString = new ParameterizedTypeReference<List<String>>() {};
        ResponseEntity<List<String>> response=restTemplate.exchange("http://example.com",HttpMethod.GET,request, listOfString);
        HttpStatus status = response.getStatusCode();
        Assert.isTrue(status == HttpStatus.OK);
    }

    // use HeaderInsertingInterceptor ...

    // tag::followRedirects[]
    @Test
    public void test_followRedirects() {
        ResponseEntity<String> response = restTemplate.exchange("http://bit.ly/1NZCQPn", HttpMethod.GET, null, String.class);
        Assert.isTrue(response.getStatusCode() == HttpStatus.OK);
    }
    // end::followRedirects[]


    // tag::test_doNotfollowRedirects[]
    @Test
    public void test_doNotfollowRedirects() {
        RestTemplate restTemplate1 = new RestTemplate(new SimpleClientHttpReqFactoryWithoutRedirects());
        ResponseEntity<String> response = restTemplate1.exchange("http://bit.ly/1NZCQPn", HttpMethod.GET, null, String.class);
        Assert.isTrue(response.getStatusCode() == HttpStatus.MOVED_PERMANENTLY);
    }

    @Test
    public void test_postForLocation() {
        URI location = restTemplate.postForLocation("http://bit.ly/1NZCQPn", null);
        System.out.println(location);
    }
    // end::test_doNotfollowRedirects[]

    /**
     * Override behaviour of {@linkplain SimpleClientHttpRequestFactory} by overriding #setInstanceFollowRedirects
     */
    public static class SimpleClientHttpReqFactoryWithoutRedirects extends SimpleClientHttpRequestFactory {
        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);
            connection.setInstanceFollowRedirects(false);
        }
    }

//    private void addAuthentication(String username, String password) {
//        if (username == null) {
//            return;
//        }
//        List<ClientHttpRequestInterceptor> interceptors = Collections
//                .<ClientHttpRequestInterceptor>singletonList(
//                        new BasicAuthorizationInterceptor(username, password));
//        setRequestFactory(new InterceptingClientHttpRequestFactory(getRequestFactory(),
//                interceptors));
//    }

    /**
     * Basic Authorization interceptor
     */
    public static class BasicAuthorizationInterceptor
            implements ClientHttpRequestInterceptor {

        private final String username;
        private final String password;

        BasicAuthorizationInterceptor(String username, String password) {
            this.username = username;
            this.password = (password == null ? "" : password);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            String token = Base64Utils.encodeToString(
                    (this.username + ":" + this.password).getBytes("UTF-8"));
            request.getHeaders().add("Authorization", "Basic " + token);
            return execution.execute(request, body);
        }

    }

    public void test_SendMultipart() throws UnsupportedEncodingException {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("name 1", "value 1");
        parts.add("name 2", "value 2+1");
        parts.add("name 2", "value 2+2");
//        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
//        parts.add("logo", logo);

        restTemplate.postForLocation("http://example.com/multipart", parts);
    }

    public void test_sendFormEncodedform() throws UnsupportedEncodingException {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("name 1", "value 1");
        form.add("name 2", "value 2+1");
        form.add("name 2", "value 2+2");

        restTemplate.postForLocation("http://example.com/form", form);
    }

    public void test_jsonObjectPost() {
//        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
//            @Override
//            public ClientHttpResponse intercept(HttpRequest hr, byte[] bytes, ClientHttpRequestExecution chre) throws IOException {
//                
//                
//                
//                return chre.execute(hr, bytes);
//            }
//        });
    }
    
    public void test_jsonViews() {
        // do your thingy
    }

    public interface PublicView {}

    public static class BigObject {

        @JsonView(PublicView.class)
        private String field1;

        private String field2;

        private String field3;

        @JsonView(PublicView.class)
        private String field4;

        public String getField4() {
            return field4;
        }

        public void setField4(String field4) {
            this.field4 = field4;
        }

        public String getField3() {
            return field3;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }
    }
    
    // DefaultResponseErrorHandler

    public void test_ConfigureTimeoutsOnDefaultRequestFactory_SimpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(1 * 1000);
        rf.setConnectTimeout(1 * 1000);
    }
    
    
    public void test_addQueryOrPathParams() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        String url = "http://example.com?applicationName={applicationName}";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("applicationName", "appName");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // first way using UriComponentsBuilder
        HttpEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);

        // second way using exchage api
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class, "appName");
    }
    
}