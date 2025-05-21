package com.fastcampus.ch2;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FortuneController {
	
	public static final String[] FORTUNE_MESSAGES = {
	        // 긍정적인 운세 (10개)
	        "오늘은 행운이 따르는 날입니다. 새로운 시도를 해보세요.",
	        "귀인을 만나게 될 수 있는 날입니다. 사람들과의 만남을 소중히 여기세요.",
	        "재물운이 상승하는 날입니다. 금전적 이득을 볼 수 있습니다.",
	        "지금까지의 노력이 결실을 맺는 날입니다. 성취감을 느끼게 될 것입니다.",
	        "건강운이 좋은 날입니다. 활기찬 하루가 될 것입니다.",
	        "사랑운이 높아지는 날입니다. 마음에 두고 있는 사람에게 다가가 보세요.",
	        "창의력이 넘치는 날입니다. 새로운 아이디어가 떠오를 것입니다.",
	        "인간관계가 개선되는 날입니다. 오해가 풀리고 화해할 수 있습니다.",
	        "자신감이 넘치는 날입니다. 도전적인 일에 도전해보세요.",
	        "직장운이 좋은 날입니다. 상사나 동료로부터 인정받을 수 있습니다.",
	        
	        // 중립적인 운세 (10개)
	        "평범한 하루가 될 것입니다. 일상을 즐기는 것이 좋습니다.",
	        "오늘은 조용히 지내는 것이 좋습니다. 내면의 목소리에 귀 기울여보세요.",
	        "생각지 못한 변화가 있을 수 있습니다. 유연하게 대처하세요.",
	        "과거의 일이 다시 떠오를 수 있습니다. 긍정적으로 해석해보세요.",
	        "결정을 내려야 할 일이 생길 수 있습니다. 신중하게 생각하세요.",
	        "주변 사람들의 조언에 귀 기울이는 것이 도움이 될 것입니다.",
	        "인과응보의 법칙이 작용하는 날입니다. 선행을 베풀어 보세요.",
	        "몸과 마음의 균형을 맞추는 것이 중요한 날입니다.",
	        "지나친 욕심은 독이 될 수 있습니다. 만족할 줄 아는 하루가 되세요.",
	        "오늘은 휴식이 필요한 날입니다. 무리하지 마세요.",
	        
	        // 주의해야 할 운세 (10개)
	        "금전 관리에 주의해야 하는 날입니다. 불필요한 지출을 자제하세요.",
	        "건강에 적신호가 있을 수 있습니다. 무리한 활동은 피하세요.",
	        "오해가 생길 수 있는 날입니다. 말과 행동에 신중하세요.",
	        "스트레스가 쌓이기 쉬운 날입니다. 마음의 여유를 가지세요.",
	        "갑작스러운 변화로 당황할 수 있습니다. 침착하게 대응하세요.",
	        "감정적인 대응은 상황을 악화시킬 수 있습니다. 이성적으로 판단하세요.",
	        "욕심을 부리면 손해를 볼 수 있습니다. 한 발 물러서는 것이 좋습니다.",
	        "주변 사람들과 마찰이 생길 수 있습니다. 양보하는 자세가 필요합니다.",
	        "예상치 못한 장애물이 나타날 수 있습니다. 인내심을 가지고 대처하세요.",
	        "오늘은 중요한 결정을 미루는 것이 좋습니다. 더 많은 정보를 수집하세요."
	    };
	
	public static String getRandomFortune() {
        int randomIndex = (int) (Math.random() * FORTUNE_MESSAGES.length);
        return FORTUNE_MESSAGES[randomIndex];
    }
	
	@RequestMapping(value = "/fortune", method = RequestMethod.GET)
	public String home(Model model) {
		
		return "fortune";
	}
	
	@RequestMapping(value = "/fortune", method = RequestMethod.POST)
	public String home(HttpServletRequest request, Model model) {
	
		String name = request.getParameter("name");
		String birth = request.getParameter("birth");
		String gender = request.getParameter("gender");
		
		String fortune = getRandomFortune();
		
		model.addAttribute("name", name);
		model.addAttribute("birth", birth);
		model.addAttribute("gender", gender);
		model.addAttribute("fortune", fortune);
		
		return "fortune_result";
	}
}
