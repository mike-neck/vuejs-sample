Vue.component("items", {
    template: "#item-template"
});

var fruits = [{
    code: "--",
    name: "--",
    price: ""
},  {
    code: "001",
    name: "りんご",
    price: 250
}, {
    code: "002",
    name: "みかん",
    price: 70
}, {
    code: "00B",
    name: "バナナ",
    price: 90
}];

var createBasket = function(f) {
    var index = f.length;
    return {
        name: "item_" + index,
        title: "商品" + (index + 1) + "個目",
        items: fruits
    };
};

var items = new Vue({
    el: "#nested",
    data: {
        title: "ワイワイショッピング",
        fruits: [{
            name: "item_0",
            title: "商品1個目",
            items: [{
                code: "--",
                name: "--",
                price: ""
            },  {
                code: "001",
                name: "りんご",
                price: 250
            }, {
                code: "002",
                name: "みかん",
                price: 70
            }, {
                code: "00B",
                name: "バナナ",
                price: 90
            }]
        }]
    },
    methods: {
        addFruit: function() {
            var list = this.fruits,
                next = createBasket(list);
            list.push(next);
        }
    }
});
