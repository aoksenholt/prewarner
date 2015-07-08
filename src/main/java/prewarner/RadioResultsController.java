package prewarner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RadioResultsController {

    @Autowired
    JdbcTemplate jt;

    @RequestMapping("/radio")
    public String greeting(@RequestParam(value="r1", required=true) Integer r1,
                           @RequestParam(value="r2", required=true) Integer r2,
                           Model model) {
        RadioResults rr = new RadioResults(jt);
        model.addAttribute("name", "between "+r1+" and " + r2);
        //model.addAttribute("runners", rr.allRunners());
        model.addAttribute("runnersBetweenRadios", rr.runnersBetween(r1, r2));
        model.addAttribute("lastAtR2", rr.lastAt(r2, 3, 60));
        model.addAttribute("r2", r2);
        model.addAttribute("r1", r1);
        return "radiowarn";
    }


}
