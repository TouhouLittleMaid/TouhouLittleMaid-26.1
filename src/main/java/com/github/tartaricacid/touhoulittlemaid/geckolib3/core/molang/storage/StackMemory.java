package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage;

import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * | ---- arg0 arg1 arg2 var0 var1 var2 ---| --- ... --- |
 *        ↑              ↑
 *        argOffset      varOffset
 */
public class StackMemory implements ITempVariableStorage {
    private static final int MAX_STACK_DEPTH = 32;

    private Object[] mem = new Object[16];
    private int varOffset;
    private int varSize;
    private int argOffset;
    private int argSize;

    // 高位存储 argSize，低位存储 argOffset
    private final LongArrayList stackFrameList = new LongArrayList(4);
    private final ArgsAccessor argsAccessor = new ArgsAccessor();

    private void ensureCapacity(int cap) {
        var mem = this.mem;
        if (mem.length < cap) {
            var newCap = mem.length * 2;
            while (newCap < cap) {
                newCap *= 2;
            }
            this.mem = Arrays.copyOf(mem, newCap);
        }
    }

    public Object getTemp(int addr) {
        if (addr < varSize) {
            return mem[varOffset + addr];
        } else {
            return null;
        }
    }

    public void setTemp(int addr, Object value) {
        var top = addr + 1;
        if (varSize < top) {
            varSize = top;
            ensureCapacity(varOffset + top);
        }
        mem[varOffset + addr] = value;
    }

    public boolean push(List<?> args) {
        if (stackFrameList.size() < MAX_STACK_DEPTH) {
            var newArgOffset = varOffset + varSize;
            var newArgSize = args.size();
            var newVarOffset = newArgOffset + newArgSize;
            ensureCapacity(newVarOffset);

            var mem = this.mem;
            for (int i = 0; i < newArgSize; i++) {
                mem[newArgOffset + i] = args.get(i);
            }

            stackFrameList.add(((long) argSize << 32) | (long) (argOffset));
            argOffset = newArgOffset;
            argSize = newArgSize;
            varOffset = newVarOffset;
            varSize = 0;

            return true;
        }
        return false;
    }

    public boolean push(ExecutionContext<?> ctx, Function.ArgumentCollection args) {
        if (stackFrameList.size() < MAX_STACK_DEPTH) {
            var newArgOffset = varOffset + varSize;
            var newArgSize = args.size();
            var newVarOffset = newArgOffset + newArgSize;
            ensureCapacity(newVarOffset);
            varSize += args.size();

            for (int i = 0; i < newArgSize; i++) {
                var value = args.getValue(ctx, i);
                this.mem[newArgOffset + i] = value;
            }

            stackFrameList.add(((long) argSize << 32) | (long) argOffset);
            argOffset = newArgOffset;
            argSize = newArgSize;
            varOffset = newVarOffset;
            varSize = 0;

            return true;
        }
        return false;
    }

    public void pop() {
        var stackFrameList = this.stackFrameList;
        if (!stackFrameList.isEmpty()) {
            var frame = stackFrameList.removeLong(stackFrameList.size() - 1);
            var oldArgOffset = argOffset;
            var newArgOffset = (int) (frame & 0x00000000FFFFFFFFL);
            var newArgSize = (int) (frame >> 32);
            var newVarOffset = newArgOffset + newArgSize;

            argOffset = newArgOffset;
            argSize = newArgSize;
            varOffset = newVarOffset;
            varSize = oldArgOffset - newVarOffset;
        }
    }

    public List<Object> argsAccessor() {
        return argsAccessor;
    }

    class ArgsIterator implements Iterator<Object> {
        private int ptr = argOffset;
        private final int end = varOffset;

        @Override
        public boolean hasNext() {
            return ptr < end;
        }

        @Override
        public Object next() {
            if (ptr < end) {
                return mem[ptr++];
            }
            return null;
        }
    }

    class ArgsAccessor implements List<Object> {
        @Override
        public int size() {
            return argSize;
        }

        @Override
        public boolean isEmpty() {
            return argSize == 0;
        }

        @Override
        public Object get(int index) {
            if (index >= 0 && index < argSize) {
                return mem[argOffset + index];
            }
            return null;
        }

        @Override
        public @NotNull Iterator<Object> iterator() {
            return new ArgsIterator();
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull Object @NotNull [] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull <T> T @NotNull [] toArray(@NotNull T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(@NotNull Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object set(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull ListIterator<Object> listIterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull ListIterator<Object> listIterator(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull List<Object> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }
    }
}
