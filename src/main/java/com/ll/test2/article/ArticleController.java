package com.ll.test2.article;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/article/list")
    public String list(Model model) {
        List<Article> articleList = this.articleService.getList();
        model.addAttribute("articleList", articleList);
        return "article_list";
    }

    @GetMapping("/article/create")
    public String create(ArticleForm articleForm) {
        return "article_form";
    }

    @PostMapping("/article/create")
    public String create(@RequestParam String title, @RequestParam String content, @Valid ArticleForm articleForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "article_form";
        }
        this.articleService.create(articleForm.getTitle(), articleForm.getContent());
        return "redirect:/article/list";
    }

    @GetMapping("/article/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Article article = this.articleService.getArticle(id);
        model.addAttribute("article", article);
        return "article_detail";
    }

    @GetMapping("/article/modify/{id}")
    public String modify(ArticleForm articleForm, @PathVariable("id") Integer id) {
        Article article = this.articleService.getArticle(id);
        articleForm.setTitle(article.getTitle());
        articleForm.setContent(article.getContent());
        return "question_form";
    }
//    @PostMapping("/article/modify/{id}")
//    public String modify2(ArticleForm articleForm, @PathVariable("id") Integer id) {
//        this.articleService.modify(article, articleForm.getTitle(), articleForm.getContent());
//        return String.format("redirect:/article/detail/%s", id);
//    }
//
//    @GetMapping("/article/delete/{id}")
//    public String delete(Principal principal, @PathVariable("id") Integer id) {
//        Article article = this.articleService.getArticle(id);
//        this.articleService.delete(article);
//        return "redirect:/";
//    }

}
