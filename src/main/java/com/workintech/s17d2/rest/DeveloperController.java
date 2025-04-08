package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private final Taxable developerTax;

    // 👇 TEST İÇİN protected yapıldı
    public Map<Integer, Developer> developers;

    @Autowired
    public DeveloperController(Taxable developerTax) {
        this.developerTax = developerTax;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer create(@RequestBody Developer developer) {
        Developer newDev = null;
        double salary = developer.getSalary();

        switch (developer.getExperience()) {
            case JUNIOR -> {
                salary -= salary * (developerTax.getSimpleTaxRate() / 100);
                newDev = new JuniorDeveloper(developer.getId(), developer.getName(), salary);
            }
            case MID -> {
                salary -= salary * (developerTax.getMiddleTaxRate() / 100);
                newDev = new MidDeveloper(developer.getId(), developer.getName(), salary);
            }
            case SENIOR -> {
                salary -= salary * (developerTax.getUpperTaxRate() / 100);
                newDev = new SeniorDeveloper(developer.getId(), developer.getName(), salary);
            }
        }

        developers.put(developer.getId(), newDev);
        return newDev;
    }

    @PutMapping("/{id}")
    public Developer update(@PathVariable int id, @RequestBody Developer updatedDeveloper) {
        developers.put(id, updatedDeveloper);
        return updatedDeveloper;
    }

    @DeleteMapping("/{id}")
    public Developer delete(@PathVariable int id) {
        return developers.remove(id);
    }
}
