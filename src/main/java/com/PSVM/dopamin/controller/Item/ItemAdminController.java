package com.PSVM.dopamin.controller.Item;

import com.PSVM.dopamin.domain.Item.*;
import com.PSVM.dopamin.error.Message;
import com.PSVM.dopamin.service.Item.ItemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;


@Controller
@RequestMapping(value="/item")
public class ItemAdminController {
    @Autowired
    private ItemAdminService itemAdminService;
    @ExceptionHandler(ItemValidatorException.class)
    @ResponseBody
    public Map catcher1(ItemValidatorException ve){
        System.out.println(ve.getMessage());
        return ve.getError_msg();
    }
    @InitBinder
    public void ItemValidator(WebDataBinder binder){
        binder.setValidator(new ItemValidator ());
    }//webdatabinder에 validator 추가

    @GetMapping("/")//상점 메인 페이지
    public String item_main(Model m,RedirectAttributes redirectAttributes){//각 아이템 항목별 TOP5를 보여주기
        //통계 각 항목별 TOP4 가져와 보여주면 됨.
        try {
            List<ItemDto> list=itemAdminService.getPage("스킨");
            //List<ItemDto> list2=itemAdminService.getPage("꾸미기");
           // System.out.println("list2 = " + list2);
            m.addAttribute("list",list);
            //m.addAttribute("list2",list2);
            return "Item/cart_main2";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/ite1m")//메인페이지 인기글 8개씩 가져옴
    public ResponseEntity<List<ItemDto>> get_pop(Integer num){
        List<ItemDto> list=null;
        try{
            list=itemAdminService.get_pop(num);
            System.out.println("list.size() = " + list.size());
            return new ResponseEntity<List<ItemDto>>(list,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<List<ItemDto>>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/list/{order}")
    public String list(@PathVariable String order,Model m, RedirectAttributes redirectAttributes){
        //로그인 여부와 상관없이
        //메인에서 <스킨> 누르면 스킨에 해당하는 아이템 목록 스킨==1
        //메인에서 <꾸미기> 누르면 꾸미이게 해당하는 아이템 목록 보여주기 꾸미기==2
        //그외 나머지는 예외처리해야함.
        try{
            if(Objects.equals(order,"스킨")||Objects.equals(order,"꾸미기")){
                int totalCnt= itemAdminService.getCount();
                if(totalCnt==0){
                    throw new Exception("보여질 아이템이 없습니다.");
                }
                Map map=new HashMap<>();
                m.addAttribute("totalCnt",totalCnt);
                List<ItemDto> list= itemAdminService.getPage(order);//ItemDto list에다가 order에 해당하는 아이템들 받아올 거임.
                m.addAttribute("list",list);
                m.addAttribute("order",order);
                return "Item/new_itemlist";
            }
            else{
                throw new Exception("잘못된 요청입니다");
            }
        }
        catch(Exception e){
            String msg=e.getMessage();
            System.out.println("msg = " + msg);
            redirectAttributes.addFlashAttribute("msg",msg);
            return "Item/new_itemlist";
        }
    }
    @GetMapping("/item_admin")
    public String item_admin(Model m, HttpServletRequest request) throws Exception{
        //관리자가에게만 버튼 보이는데
        //관리자가 아닌 사람이 url로 접근하려려고 할때, 권한 확인해서 "접근할 수 없는 페이지"입니다. 출력
        //여기서 고민해봐야 할 것들이. "접근할 수 없는 페이지"입니다. 라는 메시지를 띄우는게 좋을까?
        //어떠한 행위를 한다는 것 자체가, 어떤 유저에게는 "아 이 페이지에 뭐가 있구나" 알고,
        //어떻게든 접근하려하지 않을까?
        //그럴바에는 해당 ur로 접근 시, 관리자가 아니라면 에러메시지 보단, 상점 메인페이지 반환하자.
        if(check_Admin(request)){
            return "redirect:/item/";
        }
        try{
            List<ItemDto> list_0= itemAdminService.getStat_0();//상태 0이 비공개
            List<ItemDto> list_1= itemAdminService.getStat_1();//상태 1이 공개
            m.addAttribute("list_0",list_0);
            m.addAttribute("list_1",list_1);
            m.addAttribute("mode","list");
            return "Item/new_admin"; //조회와 수정에 사용.
            // 조회에 사용시-> mode:list;
            // 수정에 사용시-> mode:change,
        }
        catch(Exception e){
            throw new Exception("잘못된 요청입니다.");
        }
    }
    @GetMapping(value="/register")
    public String write(Model m,HttpServletRequest request){
        if(check_Admin(request)){
            return "redirect:/item/";
        }//관리자가 아니라면 비관리자들한테 이페이지의 존재 유무를 알릴 필요가 없기 때문에
        //item 메인화면으로 돌아간다.
        return "Item/item_register";
    }
    @PostMapping("/registerItem")
    public String write(@RequestPart(value="item_img",required = false) MultipartFile file, @Valid @RequestPart(value="key") ItemForm itemForm, BindingResult bindingResult) throws Exception{
        System.out.println(bindingResult);
        if(bindingResult.hasErrors()){
            throw new ItemValidatorException(bindingResult,"검증실패11");
        }
        String user_id="ldhoon0813";
        String user_nic="후후른훈";
        Map<String,String> map = new HashMap<>();//map에다가 데이터 담아서 이동
        map.put("user_id",user_id);
        map.put("user_nic",user_nic);
        int result= itemAdminService.registerItem(itemForm,file,map);
        if(result==1){
            return "Item/item_admin";
        }
        else{
            return "Item/item_admin"; //수정 필요
        }
//        try {
//            //HttpSession session=request.getSession();
//            //String user_id=(String) session.getAttribute("id");
//            //String user_nic=itemService.getUser_nic(user_id);
//            //아직은 세션없으니깐 임시로 만들자
//            String user_id="ldhoon0813";
//            String user_nic="후후른훈";
//            Map<String,String> map = new HashMap<>();//map에다가 데이터 담아서 이동
//            map.put("user_id",user_id);
//            map.put("user_nic",user_nic);
//            int result=itemService.registerItem(itemForm,file,map);
//            if(result==1){
//                return "item_admin";
//            }
//            else{
//                throw new Exception("아이템 등록에 실패했습니다.");
//            }
//            //추가로 해야할 것.
//            //1. 이름이 같은 아이템은 세상에 없다. 이름이 같은 아이템을 들어가지 못하게 하기
//            //2. 이미지 미리보기 + 화면에 이미지 띄우기.
//
//        } catch (Exception e) {
//            String msg=e.getMessage();
//            redirectAttributes.addFlashAttribute("msg",msg);
//            return "redirect:/item/registerItem";
//        }
    }
    @DeleteMapping("/remove/{item_id}")//삭제니깐 delete매핑
    @ResponseBody
    public ResponseEntity<Object> remove(@PathVariable Integer item_id){
        try {
            int row_cnt= itemAdminService.remove(item_id);//삭제 성공 시 1반환
            if(row_cnt!=1){
                throw new Exception("삭제에 실패했습니다. 잠시 후 다시 시도해 주세요.");
            }
            return new ResponseEntity<>("삭제에 성공했습니다.", HttpStatus.OK);
        } catch (Exception e) {
            Message message = Message.builder()
                    .message1(e.getMessage())
                    .build();
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/noshowtoshow/{item_id}")//비공개를 공개로
    @ResponseBody
    public ResponseEntity<Object> show(@PathVariable Integer item_id){
        try {
            int result= itemAdminService.show(item_id);
            if(result!=1){
                throw new Exception("공개에 실패했습니다. 잠시 후 다시 시도해 주세요.");
            }
            return new ResponseEntity<>("공개에 성공했습니다.",HttpStatus.OK);
        } catch (Exception e) {
            Message message = Message.builder()
                    .message1(e.getMessage())
                    .build();
            return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/showtonoshow/{item_id}")//공개를 비공개로
    @ResponseBody
    public ResponseEntity<Object> noShow(@PathVariable Integer item_id){
        try {
            int result= itemAdminService.noShow(item_id);
            if(result!=1){
                throw new Exception("비공개에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
            return new ResponseEntity<>("비공개에 성공했습니다.",HttpStatus.OK);
        } catch (Exception e) {
            Message message = Message.builder()
                    .message1(e.getMessage())
                    .build();
            return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/modify")
    @ResponseBody
    public ResponseEntity<Object> modify(@RequestBody ItemForm itemForm){
        String[] grd_nm={"1등급","2등급","3등급","4등급","5등급"};
        try {
            //입력받은 데이터에 대해서 백단에서 검증이 필요함.
            if(itemForm.getItem_nm()==null){
                throw new Exception("아이템 이름을 입력해 주세요.");
            }
            if(!Arrays.asList(grd_nm).contains(itemForm.getItem_grd())){
                throw new Exception("등급 양식은 0등급입니다");
            }
            if(itemForm.getItem_dsc()==null){
                throw new Exception("설명을 작성해 주세요.");
            }
            if(itemForm.getItem_dsc().length()>30){
                throw new Exception("설명은 30글자 이하입니다.");
            }
            if(!itemForm.getItem_price().matches("[+-]?\\d*(\\.\\d+)?")){
                throw new Exception("가격은 숫자만 입력 가능합니다.");
            }
            int result= itemAdminService.modify(itemForm);
            if(result!=1){
                throw new Exception("잠시후 다시 등록해 주세요");
            }
            return new ResponseEntity<>("아이템 수정에 성공했습니다.",HttpStatus.OK);
        } catch (Exception e) {
            Message message = Message.builder()
                    .message1(e.getMessage())
                    .build();
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }
    //관리자 인증 구현을 했지만, 아직 세션이 없어 NullpointException Error 터짐
    private boolean check_Admin(HttpServletRequest request) {
//        HttpSession httpsession= request.getSession();
//        String user_id=(String) httpsession.getAttribute("id");
//        int user_stat=itemService.getUser_stat(user_id);
//        if(user_stat==0){
//            return true;
//        }
        return false;
    }
}
