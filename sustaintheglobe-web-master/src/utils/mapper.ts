/* eslint-disable @typescript-eslint/no-explicit-any */
import ic_cat_energy from "../assets/icons/ic_cat_energy.svg";
import ic_cat_waste from "../assets/icons/ic_cat_waste.svg";
import ic_cat_environment from "../assets/icons/ic_cat_environment.svg";
import ic_cat_transportation from "../assets/icons/ic_cat_transportation.svg";
import ic_cat_water from "../assets/icons/ic_cat_water.svg";
import ic_cat_food from "../assets/icons/ic_cat_food.svg";
import ic_cat_community from "../assets/icons/ic_cat_community.svg";
import ic_cat_shopping from "../assets/icons/ic_cat_shopping.svg";
import ic_cat_education from "../assets/icons/ic_cat_education.svg";
import ic_cat_social from "../assets/icons/ic_cat_social.svg";
import ic_cat_default from "../assets/icons/ic_cat_default.svg";
const categoryImages: any = {
  "energy conservation": ic_cat_energy,
  "waste reduction": ic_cat_waste,
  "environmental preservation": ic_cat_environment,
  "sustainable transportation": ic_cat_transportation,
  "water conservation": ic_cat_water,
  "food sustainability": ic_cat_food,
  "community engagement": ic_cat_community,
  "sustainable shopping": ic_cat_shopping,
  "education and awareness": ic_cat_education,
  "social responsibility": ic_cat_social,
  "default icon": ic_cat_default,
};

export const getCategoryImagePath = (category: string) => {
  return (
    categoryImages[category.toLowerCase()] ?? categoryImages["default icon"]
  );
};
