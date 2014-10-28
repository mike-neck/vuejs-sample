var todo = new Vue({
    el: "#todo",
    data: {
        title: "todos",
        // data type
        // done: boolean
        // content: string
        todos: []
    },
    methods: {
        changeOrder: function(e) {
            this.todos.sort(function(left, right){
                var lc = left.content, ld = left.done, lt = left.time,
                    rc = right.content, rd = right.done, rt = right.time;
                if (ld !== rd) {
                    return ld? 1:-1;
                } else if (lt !== rt) {
                    return lt - rt;
                } else {
                    return lc < rc? -1: lc > rc? 1:0;
                }
            });
        }
    }
});

var input = new Vue({
    el: "#input",
    data: {
        something: ""
    },
    methods: {
        addTodo: function(e) {
            var now = (new Date()).getTime();
            todo.todos.push({
                done: false,
                content: this.something,
                time: now
            });
            this.something = "";
        }
    }
});
