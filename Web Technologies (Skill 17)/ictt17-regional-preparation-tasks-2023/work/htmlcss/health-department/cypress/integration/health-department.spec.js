/// <reference types="Cypress" />

context("Health Department", () => {
  before(() => {
    cy.visit("/health-department.html");
  });

  beforeEach(() => {
    cy.get("body").as("body");
    cy.get(".content").as("content");
    cy.get(".service").as("service");
    cy.get(".service li").eq(0).as("firstService");
    cy.get(".logo img").as("logo");
    cy.get(".logo h1").as("title");
    cy.get(".breadcrumb").as("breadcrumb");
    cy.get("nav").as("nav");
    cy.get("nav ul li:first-child").as("firstNav");
    cy.get("section").as("section");
    cy.get(".left").as("left");
    cy.get(".article").eq(0).as("firstArticle");
    cy.get(".article:last-child").as("lastArticle");
    cy.get(".media:last-child").as("lastMedia");
    cy.get(".media").eq(0).as("firstMedia");
  });

  it("body", function () {
    // SPACING
    cy.get("body")
      .X()
      .should("be.closeTo", 0, 1, "Expected body to not have any spacings");

    // STYLING
    cy.get("body")
      .should("exist")
      .should("be.visible")
      .should("have.css", "font-family", "Helvetica")
      .should("have.css", "font-size", "13px")
      .should("have.css", "color", "rgb(69, 69, 69)")
      .should("have.css", "background-image")
      .and("match", /url\(".*\/assets\/background\.png"\)$/);
  });

  it("content", function () {
    // SIZING + SPACING
    cy.get(".content")
      .width()
      .should(
        "be.closeTo",
        this.body[0].getBoundingClientRect().width * 0.875,
        1,
        "Expected content to be 8.75 / 10 of the size of the body"
      );

    cy.get(".content")
      .Y()
      .should("eq", 20, "Expected content to have 20px spacing from the top");

    cy.get(".content")
      .endY()
      .should(
        "be.closeTo",
        this.body[0].getBoundingClientRect().bottom - 20,
        1,
        "Expected content to have 20px spacing from the bottom"
      );

    cy.get("body")
      .centerX()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().left +
          this.content[0].getBoundingClientRect().width / 2,
        1,
        "Expected content to be centered"
      );

    // STYLING
    cy.get(".content")
      .should("exist")
      .should("be.visible")
      .should("have.css", "background-color", "rgb(255, 255, 255)")
      .should("have.css", "box-shadow", "rgba(0, 0, 0, 0.6) 0px 0px 60px 0px")
      .should("have.css", "min-width", "850px");
  });

  it("top header", function () {
    // SPACING
    cy.get(".breadcrumb")
      .should("exist")
      .should("be.visible")
      .Y()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().top + 15,
        1,
        "Expected breadcrumb to have 15px spacing from content top"
      );

    cy.get(".breadcrumb")
      .should("exist")
      .should("be.visible")
      .X()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().left + 15,
        1,
        "Expected breadcrumb to have 15px spacing from the left side of content"
      );

    cy.get(".languages")
      .should("exist")
      .should("be.visible")
      .endX()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().right - 15,
        1,
        "Expected language links part of header to have 15px spacing from the right side of content"
      );

    cy.get(".service li")
      .eq(1)
      .X()
      .should(
        "be.closeTo",
        this.firstService[0].getBoundingClientRect().right + 15,
        1,
        "Expected service links to have 15px spacing between them"
      );

    cy.get(".service li")
      .eq(0)
      .centerY()
      .should(
        "be.closeTo",
        this.breadcrumb[0].getBoundingClientRect().top +
          this.breadcrumb[0].getBoundingClientRect().height / 2,
        1,
        "Expected service links to be vertically centered with breadcrumbs"
      );

    cy.get(".languages")
      .X()
      .should(
        "be.closeTo",
        this.service[0].getBoundingClientRect().right + 15,
        1,
        "Expected language links to have 15px spacing from service links"
      );

    cy.get(".languages")
      .centerY()
      .should(
        "be.closeTo",
        this.breadcrumb[0].getBoundingClientRect().top +
          this.breadcrumb[0].getBoundingClientRect().height / 2,
        1,
        "Expected language links to be vertically centered with breadcrumbs"
      );

    // STYLING
    cy.get(".service")
      .should("exist")
      .should("be.visible")
      .should("have.css", "font-size", "11px");

    cy.get(".languages")
      .should("exist")
      .should("be.visible")
      .should("have.css", "border-left", "1px solid rgb(229, 229, 229)")
      .should("have.css", "padding-left", "15px");

    cy.get(".languages li.active")
      .should("exist")
      .should("be.visible")
      .should("have.css", "background-color", "rgb(229, 229, 229)");
  });

  xit("(hard) breadcrumbs", function () {
    // (˳˘ ɜ˘)♬♪♫ -- Nothing to see here...
    throw "𝘩𝘪𝘥𝘥𝘦𝘯 Expected breadcrumbs to be styled correctly";
  });

  it("bottom header", function () {
    // SPACING
    cy.get(".logo")
      .should("exist")
      .should("be.visible")
      .Y()
      .should(
        "be.closeTo",
        this.breadcrumb[0].getBoundingClientRect().top +
          this.breadcrumb[0].getBoundingClientRect().height +
          15,
        1,
        "Expected logo to have 15px spacing from breadcrumb"
      );

    cy.get(".logo img")
      .X()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().left + 15,
        1,
        "Expected logo to be correctly positioned on the left in the header"
      );

    cy.get(".logo h1")
      .should("exist")
      .should("be.visible")
      .X()
      .should(
        "be.closeTo",
        this.logo[0].getBoundingClientRect().left +
          this.logo[0].getBoundingClientRect().width +
          32,
        1,
        "Expected h1 to have 32px spacing from logo"
      );

    cy.get(".search")
      .should("exist")
      .should("be.visible")
      .endX()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().right - 15,
        1,
        "Expected search element to have 15px spacing from content"
      );

    cy.get(".search")
      .X()
      .should(
        "be.closeTo",
        this.title[0].getBoundingClientRect().left +
          this.title[0].getBoundingClientRect().width +
          32,
        1,
        "Expected search to have 32px spacing between logo"
      );

    // STYLING
    cy.get(".logo img")
      .should("exist")
      .should("be.visible")
      .should("have.css", "width", "254px")
      .should("have.css", "padding-right", "32px")
      .should("have.css", "border-right", "1px solid rgb(229, 229, 229)");

    cy.get(".logo h1")
      .should("exist")
      .should("be.visible")
      .should("have.css", "color", "rgb(0, 0, 0)")
      .should("have.css", "font-weight", "700")
      .should("have.css", "font-size", "14px");

    cy.get(".search input")
      .should("exist")
      .should("be.visible")
      .should("have.css", "max-width", "100px");

    cy.get(".search").then(($el) => {
      const before = window.getComputedStyle($el[0], "::before");

      const background = before.getPropertyValue("background-image");
      const backgroundRepeat = before.getPropertyValue("background-repeat");
      const backgroundPosition = before.getPropertyValue("background-position");
      expect(background).to.match(/url\(".*\/assets\/search\.svg"\)$/);
      expect(backgroundRepeat).to.eq("no-repeat");
      expect(backgroundPosition).to.eq("50% 50%");

      const content = before.getPropertyValue("content");
      const width = before.getPropertyValue("width");
      const height = before.getPropertyValue("height");
      const position = before.getPropertyValue("position");
      const padding = before.getPropertyValue("padding");

      expect(content).to.eq('""');
      expect(width).to.eq("18px");
      expect(height).to.eq("18px");
      expect(position).to.eq("absolute");
      expect(padding).to.not.eq("0px");
    });

    cy.get(".search input")
      .should("exist")
      .should("be.visible")
      .should("have.css", "padding", "0px 30px")
      .should("have.css", "height", "25px")
      .should("have.css", "border", "1px solid rgb(204, 204, 204)")
      .should("have.css", "border-radius", "4px");
  });

  it("nav", function () {
    // SPACING
    cy.get("nav")
      .should("exist")
      .should("be.visible")
      .Y()
      .should(
        "be.closeTo",
        this.logo[0].getBoundingClientRect().top +
          this.logo[0].getBoundingClientRect().height +
          32,
        1,
        "Expected nav to have 32px spacing from logo"
      );

    cy.get("nav ul li:first-child")
      .X()
      .should(
        "be.closeTo",
        this.nav[0].getBoundingClientRect().left + 32,
        2,
        "Expected first nav item to have 32px spacing from nav"
      );

    cy.get("nav ul li:last-child")
      .endX()
      .should(
        "be.closeTo",
        this.nav[0].getBoundingClientRect().right - 32,
        2,
        "Expected last nav item to have 32px spacing from nav"
      );

    cy.get("nav ul li:last-child")
      .Y()
      .should(
        "be.closeTo",
        this.firstNav[0].getBoundingClientRect().top,
        1,
        "Expected first and last item to be on the same line"
      );

    // STYLING
    cy.get("nav").should("have.css", "border-top", "5px solid rgb(220, 0, 24)");

    cy.get("nav ul")
      .should("exist")
      .should("be.visible")
      .should("have.css", "border-left", "1px solid rgb(184, 184, 184)");

    cy.get("nav ul li")
      .eq(5)
      .should("exist")
      .should("be.visible")
      .should("have.css", "height", "70px")
      .should("have.css", "padding", "15px")
      .should("have.css", "border-right", "1px solid rgb(184, 184, 184)")
      .should("have.css", "text-align", "center")
      .should("have.css", "display", "flex")
      .should("have.css", "align-items", "center");

    cy.get("nav ul li")
      .eq(5)
      .then(($el) => {
        const after = window.getComputedStyle($el[0], "::after");

        const background = after.getPropertyValue("background-image");
        expect(background).to.match(
          /url\(".*\/assets\/triangle\-bottom\.svg"\)$/
        );

        const content = after.getPropertyValue("content");
        const width = after.getPropertyValue("width");
        const height = after.getPropertyValue("height");
        const marginTop = after.getPropertyValue("margin-top");

        expect(content).to.eq('""');
        expect(width).to.eq("8px");
        expect(height).to.eq("4px");
        expect(marginTop).to.not.eq("0px");
      });
  });

  it("left panel", function () {
    // SPACING
    cy.get("section .left")
      .X()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().left + 15,
        1,
        "Expected left panel to have 15px spacing from the left side"
      );

    cy.get("section .left")
      .width()
      .should(
        "be.closeTo",
        this.section[0].getBoundingClientRect().width * 0.75 - 45,
        1,
        "Expected left side to be 3/4 of the section width"
      );

    // STYLING
    cy.get("section").should("exist").should("be.visible");
    cy.get("section .left").should("exist").should("be.visible");
    cy.get("section .left h2")
      .should("exist")
      .should("be.visible")
      .should("have.css", "font-size", "16px")
      .should("have.css", "border-bottom", "2px solid rgb(204, 204, 204)")
      .should("have.css", "height", "30px");
  });

  it("right panel & 'all' link", function () {
    // SPACING
    cy.get("section .right")
      .width()
      .should(
        "be.closeTo",
        this.section[0].getBoundingClientRect().width * 0.25 - 15,
        1,
        "Expected right side to be 1/4 of the section width"
      );

    // STYLING
    cy.get("section").should("exist").should("be.visible");
    cy.get("section .right").should("exist").should("be.visible");
    cy.get("section .right h2")
      .should("exist")
      .should("be.visible")
      .should("have.css", "font-size", "16px")
      .should("have.css", "border-bottom", "2px solid rgb(204, 204, 204)")
      .should("have.css", "height", "30px");

    cy.get(".all")
      .should("exist")
      .should("be.visible")
      .should("have.css", "color", "rgb(0, 102, 153)")
      .then(($el) => {
        const before = window.getComputedStyle($el[0], "::before");

        const background = before.getPropertyValue("background-image");
        const backgroundRepeat = before.getPropertyValue("background-repeat");
        const backgroundPosition = before.getPropertyValue(
          "background-position"
        );
        expect(background).to.match(/url\(".*\/assets\/list\.svg"\)$/);
        expect(backgroundRepeat).to.eq("no-repeat");
        expect(backgroundPosition).to.eq("50% 50%");

        const content = before.getPropertyValue("content");
        const width = before.getPropertyValue("width");
        const height = before.getPropertyValue("height");

        expect(content).to.eq('""');
        expect(width).to.eq("10px");
        expect(height).to.eq("10px");
      });
  });

  it("articles in left panel", function () {
    // SPACING
    cy.get(".article")
      .eq(0)
      .Y()
      .should(
        "be.closeTo",
        this.lastArticle[0].getBoundingClientRect().top,
        1,
        "Expected articles to be on the same line"
      );

    cy.get("section h2")
      .eq(0)
      .endY()
      .should(
        "be.closeTo",
        this.firstArticle[0].getBoundingClientRect().top - 15,
        1,
        "Expected first article to have 15px spacing from title"
      );

    cy.get(".article")
      .eq(1)
      .endX()
      .should(
        "be.closeTo",
        this.lastArticle[0].getBoundingClientRect().left - 15,
        1,
        "Expected articles to have 15px spacing between eachother"
      );

    cy.get(".article img")
      .eq(2)
      .width()
      .should(
        "be.closeTo",
        this.lastArticle[0].getBoundingClientRect().width,
        1,
        "Expected image to be full width of article"
      );

    // STYLING
    cy.get(".article")
      .should("exist")
      .should("be.visible")
      .should("have.css", "min-width", "150px");

    cy.get(".article h3")
      .eq(2)
      .should("exist")
      .should("be.visible")
      .should("have.css", "color", "rgb(0, 102, 153)")
      .should("have.css", "font-weight", "400")
      .should("have.css", "font-size", "16px");

    cy.get(".article p")
      .eq(2)
      .should("exist")
      .should("be.visible")
      .should("have.css", "font-size", "13px");
  });

  it("media in right panel", function () {
    // SPACING
    cy.get("section h2")
      .eq(1)
      .endY()
      .should(
        "be.closeTo",
        this.firstMedia[0].getBoundingClientRect().top - 15,
        1,
        "Expected first media to have 15px spacing from title"
      );

    cy.get(".media")
      .eq(1)
      .endY()
      .should(
        "be.closeTo",
        this.lastMedia[0].getBoundingClientRect().bottom -
          this.lastMedia[0].getBoundingClientRect().height -
          15,
        1,
        "Expected media to have 15px spacing between eachother"
      );

    // STYLING
    cy.get(".media")
      .eq(0)
      .should("exist")
      .should("be.visible")
      .should("have.css", "border-bottom", "1px solid rgb(204, 204, 204)")
      .should("have.css", "padding-bottom", "15px");

    cy.get(".media:last-child")
      .should("exist")
      .should("be.visible")
      .should("have.css", "border-bottom", "0px none rgb(69, 69, 69)")
      .should("have.css", "padding-bottom", "0px")
      .should("have.css", "margin-bottom", "0px");

    cy.get(".media h4")
      .eq(0)
      .should("exist")
      .should("be.visible")
      .should("have.css", "color", "rgb(0, 102, 153)")
      .should("have.css", "font-size", "15px")
      .should("have.css", "font-weight", "400");

    cy.get(".media p")
      .eq(2)
      .should("exist")
      .should("be.visible")
      .should("have.css", "font-size", "13px");
  });

  it("footer", function () {
    // SPACING
    cy.get("footer")
      .Y()
      .should(
        "be.closeTo",
        this.section[0].getBoundingClientRect().top +
          this.section[0].getBoundingClientRect().height +
          32,
        1,
        "Expected footer to be 32px below section"
      );

    cy.get("footer span")
      .should("exist")
      .should("be.visible")
      .X()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().left + 15,
        1,
        "Expected footer text to be correctly positioned on the left side"
      );

    cy.get("footer ul")
      .should("exist")
      .should("be.visible")
      .endX()
      .should(
        "be.closeTo",
        this.content[0].getBoundingClientRect().right - 15,
        1,
        "Expected footer links to be correctly positioned on the right side"
      );

    // STYLING
    cy.get("footer")
      .should("exist")
      .should("be.visible")
      .should("have.css", "border-top", "1px solid rgb(213, 213, 213)")
      .should("have.css", "background-color", "rgb(245, 245, 245)");

    cy.get("footer li")
      .should("exist")
      .should("be.visible")
      .should("have.css", "color", "rgb(0, 102, 153)");
  });
});
