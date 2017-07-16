import com.binara.entity.User;
import com.binara.repository.UserRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by binara on 7/16/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {UserRepositoryTest.class, UserRepository.class, User.class})
public class UserRepositoryTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy filterChain;

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mvc = webAppContextSetup(this.context).addFilters(this.filterChain).build();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testUserRepository() throws Exception {
        String header = "Basic "
                + new String(Base64.getEncoder().encode("foo:bar".getBytes()));
        MvcResult result = this.mvc
                .perform(post("/oauth/token").header("Authorization", header)
                        .param("grant_type", "password").param("scope", "read")
                        .param("username", "greg").param("password", "turnquist"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        Object accessToken = this.objectMapper
                .readValue(result.getResponse().getContentAsString(), Map.class)
                .get("access_token");
        MvcResult flightsAction = this.mvc
                .perform(get("/users/14").accept(MediaTypes.HAL_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(header().string("Content-Type",
                        MediaTypes.HAL_JSON.toString() + ";charset=UTF-8"))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        User user = this.objectMapper.readValue(
                flightsAction.getResponse().getContentAsString(), User.class);

        assertThat(user.getId()).isEqualTo(14);
        assertThat(user.getName()).isEqualTo("Karl Jackson");
        assertThat(user.getUsername()).isEqualTo("karl");
        assertThat(user.getPassword()).isEqualTo("karl123");
        assertThat(user.getRole().getId()).isEqualTo(4);
        assertThat(user.getRole().getName()).isEqualTo("CUSTOMER");
    }

}
