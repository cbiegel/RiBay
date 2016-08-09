Array.prototype.toMap = function (keyFunction, valueFunction) {
    var result = new Map();
    this.forEach(function (elem) {
        var key = keyFunction(elem);
        var value = valueFunction(elem);
        result.set(key, value);
    });
    return result;
};

// returns this map without the other map (identified by their keys)
Map.prototype.without = function (otherMap) {
    var result = new Map();
    this.forEach(function (value, key) {
        // the result only contains pairs that are in this map but not in the other map
        if (!otherMap.has(key)) {
            result.set(key, value);
        }
    });
    return result;
};

Map.prototype.valueList = function () {
    var result = [];
    this.forEach(function (value) {
        result.push(value);
    });
    return result;
};
