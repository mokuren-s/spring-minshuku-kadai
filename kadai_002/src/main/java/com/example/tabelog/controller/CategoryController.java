package com.example.tabelog.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Category;
import com.example.tabelog.form.CategoryEditForm;
import com.example.tabelog.form.CategoryRegisterForm;
import com.example.tabelog.repository.CategoryRepository;
import com.example.tabelog.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;

	public CategoryController(CategoryRepository categoryRepository, CategoryService categoryService) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			            Model model) {

		Page<Category> categoryPage;
		
		if (keyword != null && !keyword.isEmpty()) {
	    	
	        categoryPage = categoryRepository.findByNameLike("%" + keyword + "%", pageable);
	    
	        } else {
	        	categoryPage = categoryRepository.findAll(pageable);
	        }

		model.addAttribute("categoryPage", categoryPage);
		model.addAttribute("keyword", keyword);

		return "admin/categories/index";
	}

	@GetMapping("/register")
	public String register(Model model) {
		List<Category> category = categoryRepository.findAll();

		model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
		model.addAttribute("category", category);

		return "admin/categories/register";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm,
			             BindingResult bindingResult,
			             RedirectAttributes redirectAttributes) {
		
		if (bindingResult.hasErrors()) {
			return "admin/categories/register";
		}

		categoryService.create(categoryRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");

		return "redirect:/admin/categories";

	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		
		Category category = categoryRepository.getReferenceById(id);

		//フォームクラスをインスタンス化する
		CategoryEditForm categoryEditForm = new CategoryEditForm(category.getId(), category.getName());

		//生成したインスタンスをビューに渡す
		model.addAttribute("categoryEditForm", categoryEditForm);

		return "admin/categories/edit";
	}

	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm, BindingResult bindingResult,
			             RedirectAttributes redirectAttributes) {
		
		if (bindingResult.hasErrors()) {
			return "admin/categories/edit";
		}

		categoryService.update(categoryEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを編集しました。");
		
		return "redirect:/admin/categories";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		// 対象idの情報を削除
		categoryRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");

		return "redirect:/admin/categories";
	}
}
