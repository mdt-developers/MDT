// RPG
class Inventory {
  constructor(maxWeight) {
    this.maxWeight = maxWeight;
    this.items = [];
  }

  getCurrentWeight() {
    return this.items.reduce((sum, item) => sum + item.weight, 0);
  }

  addItem(name, type, weight, value) {
    if (this.getCurrentWeight() + weight > this.maxWeight) {
      console.log(`No room for: ${name}! Too heavy.`);
      return false;
    }
    this.items.push({ name, type, weight, value });
    console.log(`Added: ${name} (Weight: ${weight}kg)`);
    return true;
  }

  findMostExpensive() {
    if (this.items.length === 0) return null;
    return this.items.reduce((max, item) => 
      item.value > max.value ? item : max
    );
  }

  filterByType(type) {
    return this.items.filter(item => item.type === type);
  }

  summary() {
    const totalValue = this.items.reduce((sum, item) => sum + item.value, 0);
    console.log(`\n--- INVENTORY SUMMARY ---`);
    console.log(`Items count: ${this.items.length}`);
    console.log(`Weight: ${this.getCurrentWeight()}/${this.maxWeight} kg`);
    console.log(`Total value: ${totalValue} gold`);
  }
}

// Practical usage of the class
const myBackpack = new Inventory(20);
myBackpack.addItem("Sword of Light", "weapon", 5, 250);
myBackpack.addItem("Health Potion", "potion", 1, 50);
myBackpack.addItem("Plate Armor", "armor", 12, 500);
myBackpack.addItem("Giant Boulder", "junk", 10, 1);

myBackpack.summary();
console.log("Most expensive item:", myBackpack.findMostExpensive().name);
