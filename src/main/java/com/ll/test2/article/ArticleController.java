package com.ll.test2.article;

import com.ll.test2.user.SiteUser;
import com.ll.test2.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    @GetMapping("/article/list")
    public String list(Model model, @RequestParam(value = "kw", defaultValue = "") String kw) {
        List<Article> articleList = this.articleService.getList(kw);
        model.addAttribute("articleList", articleList);
        model.addAttribute("kw", kw);
        return "article_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/article/create")
    public String create(ArticleForm articleForm) {
        return "article_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/article/create")
    public String create(@RequestParam String title, @RequestParam String content, @Valid ArticleForm articleForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "article_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.articleService.create(articleForm.getTitle(), articleForm.getContent(), siteUser);
        return "redirect:/article/list";
    }

    @GetMapping("/article/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Article article = this.articleService.getArticle(id);
        model.addAttribute("article", article);
        return "article_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/article/modify/{id}")
    public String modify(Model model, ArticleForm articleForm, @PathVariable("id") Integer id, Principal principal) {
        Article article = this.articleService.getArticle(id);
        articleForm.setTitle(article.getTitle());
        articleForm.setContent(article.getContent());
        return "article_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/article/modify/{id}")
    public String modify(Model model, @PathVariable("id") Integer id, @Valid ArticleForm articleForm, BindingResult bindingResult, Principal principal) {
        Article article = this.articleService.getArticle(id);

        if(bindingResult.hasErrors()) {
            return "article_form";
        }
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.articleService.modify(article, articleForm.getTitle(), articleForm.getContent());
        return String.format("redirect:/article/detail/%s", article.getId());
    }

    @GetMapping("/article/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        Article article = this.articleService.getArticle(id);
        this.articleService.delete(article);
        return "redirect:/article/list";
    }

}
